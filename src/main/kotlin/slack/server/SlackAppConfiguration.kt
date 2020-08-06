package slack.server

import com.slack.api.bolt.App
import combine
import core.model.*
import core.model.storage.LiquidPollRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.model.*
import slack.ui.create.*
import slack.ui.poll.SingleChoicePollBlockView
import java.util.*
import kotlin.IllegalArgumentException
import kotlin.coroutines.coroutineContext

// TODO: Poll view factory -- fun createPollView(type: PollType): UIRepresentable<View>
// TODO: error handling (zero poll options not allowed)
// TODO: Creation poll date
// TODO: Chat poll view with author, date, vote view
// TODO: research for types of delegation button/picker
// TODO: Support poll types switching
@Configuration
open class SlackAppConfiguration(
    private val provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository,
    private val liquidPollRepository: LiquidPollRepository
) {

    @Bean
    open fun initSlackApp(): App {
        val app = App()
        return app.apply {
            registerPollCreation(this)
        }
    }

    private fun registerPollCreation(app: App) {
        app.command("/liquid") { request, context ->
            provider.client(context.asyncClient())

            val audienceFuture = provider.usersList().combine(provider.conversationsList())
            val (users, channels) = audienceFuture.get()

            val metadata = CreationMetadata(UUID.randomUUID().toString())
            val view = CreatePollView(
                metadata,
                PollType.DEFAULT,
                listOf(),
                users,
                channels
            )

            val pollBuilder = PollBuilder(
                id = metadata.pollID,
                author = PollAuthor(request.payload.userId, request.payload.userName),
                type = PollType.SINGLE_CHOICE
            )

            creationRepository.put(metadata.pollID, pollBuilder)

            provider.openView(view, context.triggerId)

            context.ack()
        }

        app.viewSubmission(CreationConstants.CallbackID.CREATION_VIEW_SUBMISSION) { request, context ->
            provider.client(context.asyncClient())

            val state = request.payload.view.state
            val newQuestion = state.let {
                it.values[CreationConstants.BlockID.QUESTION]?.get(CreationConstants.ActionID.POLL_QUESTION)?.value
            }!!

            val audience =
                state.values[CreationConstants.BlockID.AUDIENCE]?.get(CreationConstants.ActionID.POLL_AUDIENCE)?.selectedOptions!!

            val metadata = GSON.fromJson(
                request.payload.view.privateMetadata,
                CreationMetadata::class.java
            )

            val pollBuilder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()

            if (pollBuilder.options.isEmpty()) {
                val audienceFuture = provider.usersList().combine(provider.conversationsList())
                val (users, channels) = audienceFuture.get()
                val newView = CreatePollView(
                    metadata, pollBuilder.type, pollBuilder.options, users, channels, listOf(SlackError.EmptyOptions)
                )
                provider.updateView(newView, request.payload.view.id)
                return@viewSubmission context.ackWithErrors(mutableMapOf())
            }

            val newPoll = pollBuilder.apply {
                question = newQuestion
            }.build()

            creationRepository.remove(metadata.pollID)

            liquidPollRepository.put(metadata.pollID, newPoll)

            // sending messages
            val regex = "# (\\w+)".toPattern()
            val (channels, users) = audience.fold(mutableListOf<SlackChannel>() to mutableListOf<SlackUser>()) { acc, value ->
                val matcher = regex.matcher(value.text.text)

                if (matcher.matches()) {
                    acc.first.add(SlackChannel(value.value, matcher.group(1)))
                } else {
                    acc.second.add(SlackUser(value.value, value.text.text))
                }

                acc
            }

            check(newPoll.type == PollType.SINGLE_CHOICE) // TODO: remove after poll type feature

            val pollView = SingleChoicePollBlockView(newPoll as SingleChoicePoll, mapOf())
            for (channel in channels) {
                provider.postChatMessage(pollView, channel.id)
            }

            for (user in users) {
                provider.postDirectMessage(pollView, user.id)
            }

            context.ack()
        }

        app.viewSubmission(CreationConstants.CallbackID.ADD_OPTION_VIEW_SUBMISSION) { request, context ->
            provider.client(context.asyncClient())
            val state = request.payload.view.state
            val newOptions = state.values.values.flatMap { id2StateMap ->
                id2StateMap.entries.map {
                    val id = it.key
                    val content = it.value.value
                    PollOption(id, content)
                }
            }

            val metadata = GSON.fromJson(
                request.payload.view.privateMetadata,
                CreationMetadata::class.java
            )

            val pollBuilder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()
            val newPollBuilder = pollBuilder.apply { options = newOptions }
            creationRepository.put(metadata.pollID, newPollBuilder)


            val parentViewId = request.payload.view.previousViewId
            val audienceFuture = provider.usersList().combine(provider.conversationsList())
            val (users, channels) = audienceFuture.get()
            val newView = CreatePollView(metadata, newPollBuilder.type, newPollBuilder.options, users, channels)
            provider.updateView(newView, parentViewId)
            context.ack()
        }

        app.blockAction(CreationConstants.ActionID.SINGLE_POLL_ADD_CHOICE) { request, context ->
            val metadata = GSON.fromJson(
                request.payload.view.privateMetadata,
                CreationMetadata::class.java
            )
            val pollBuilder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()

            val newPollBuilder = pollBuilder.apply {
                options = options + PollOption(UUID.randomUUID().toString(), "")
            }
            val addView = AddOptionsPollView(
                metadata,
                newPollBuilder.options
            )

            provider.client(context.asyncClient()).pushView(addView, context.triggerId)
            creationRepository.put(metadata.pollID, newPollBuilder)
            context.ack()
        }

        app.blockAction(CreationConstants.ActionID.ADD_NEW_OPTION_BUTTON) { request, context ->
            provider.client(context.asyncClient())
            val metadata = GSON.fromJson(
                request.payload.view.privateMetadata,
                CreationMetadata::class.java
            )
            val pollBuilder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()

            val newPollBuilder = pollBuilder.apply {
                options = pollBuilder.options + PollOption(UUID.randomUUID().toString(), "")
            }

            val addView = AddOptionsPollView(
                metadata,
                newPollBuilder.options
            )

            provider.updateView(addView, request.payload.view.id)
            creationRepository.put(metadata.pollID, newPollBuilder)
            context.ack()
        }

        app.blockAction(CreationConstants.ActionID.POLL_TYPE) { request, context ->
            provider.client(context.asyncClient())
            val metadata = GSON.fromJson(
                request.payload.view.privateMetadata,
                CreationMetadata::class.java
            )

            val action = request.payload.actions.first()
            val pollType = PollType.valueOf(action.selectedOption.value)
            val pollBuilder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()
            val newPollBuilder = pollBuilder.apply {
                type = pollType
            }
            creationRepository.put(metadata.pollID, newPollBuilder)

            context.ack()
        }
    }

    companion object {
        val GSON = CreationConstants.GSON
    }
}
