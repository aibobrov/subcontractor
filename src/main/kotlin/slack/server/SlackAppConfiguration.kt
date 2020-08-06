package slack.server

import com.slack.api.bolt.App
import combine
import core.model.PollAuthor
import core.model.PollOption
import core.model.PollType
import core.model.SingleChoicePoll
import core.model.base.VotingTime
import core.model.storage.LiquidPollRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.model.*
import slack.ui.create.*
import slack.ui.poll.SingleChoicePollBlockView
import java.util.*

// TODO: Poll view factory -- fun createPollView(type: PollType): UIRepresentable<View>
// TODO: error handling (zero poll options not allowed)
// TODO: Creation poll date
// TODO: Chat poll view with author, date, vote view
// TODO: research for types of delegation button/picker
// TODO: Poll builder
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

            val poll = SingleChoicePoll(
                id = metadata.pollID,
                question = "",
                description = null,
                options = listOf(),
                votingTime = VotingTime.Unlimited,
                tags = listOf(),
                isFinished = false,
                author = PollAuthor(request.payload.userId, request.payload.userName)
            )

            creationRepository.put(metadata.pollID, poll)

            provider.openView(view, context.triggerId)

            context.ack()
        }

        app.viewSubmission(CreationConstants.CallbackID.CREATION_VIEW_SUBMISSION) { request, context ->
            provider.client(context.asyncClient())
            val state = request.payload.view.state
            val question = state.let {
                it.values[CreationConstants.BlockID.QUESTION]?.get(CreationConstants.ActionID.POLL_QUESTION)?.value
            }!!

            val audience =
                state.values[CreationConstants.BlockID.AUDIENCE]?.get(CreationConstants.ActionID.POLL_AUDIENCE)?.selectedOptions!!

            val metadata = GSON.fromJson(
                request.payload.view.privateMetadata,
                CreationMetadata::class.java
            )

            val poll = creationRepository.get(metadata.pollID) as SingleChoicePoll

            val newPoll = poll.copy(question = question)

            creationRepository.remove(metadata.pollID)

            liquidPollRepository.put(metadata.pollID, newPoll)

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

            val pollView = SingleChoicePollBlockView(newPoll, mapOf())
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
            val options = state.values.values.flatMap { id2StateMap ->
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

            val poll = creationRepository.get(metadata.pollID) as SingleChoicePoll
            val newPoll = poll.copy(options = options)
            creationRepository.put(metadata.pollID, newPoll)


            val parentViewId = request.payload.view.previousViewId
            val audienceFuture = provider.usersList().combine(provider.conversationsList())
            val (users, channels) = audienceFuture.get()
            val newView = CreatePollView(metadata, newPoll.type, newPoll.options, users, channels)
            provider.updateView(newView, parentViewId)
            context.ack()
        }

        app.blockAction(CreationConstants.ActionID.SINGLE_POLL_ADD_CHOICE) { request, context ->
            val metadata = GSON.fromJson(
                request.payload.view.privateMetadata,
                CreationMetadata::class.java
            )
            val poll = creationRepository.get(metadata.pollID) as SingleChoicePoll
            val newPoll = poll.copy(options = poll.options + PollOption(UUID.randomUUID().toString(), ""))
            val addView = AddOptionsPollView(
                metadata,
                newPoll.options
            )

            provider.client(context.asyncClient()).pushView(addView, context.triggerId)
            creationRepository.put(metadata.pollID, newPoll)
            context.ack()
        }

        app.blockAction(CreationConstants.ActionID.ADD_NEW_OPTION_BUTTON) { request, context ->
            provider.client(context.asyncClient())
            val metadata = GSON.fromJson(
                request.payload.view.privateMetadata,
                CreationMetadata::class.java
            )
            val poll = creationRepository.get(metadata.pollID) as SingleChoicePoll
            val newPoll = poll.copy(options = poll.options + PollOption(UUID.randomUUID().toString(), ""))
            val addView = AddOptionsPollView(
                metadata,
                newPoll.options
            )

            provider.updateView(addView, request.payload.view.id)
            creationRepository.put(metadata.pollID, newPoll)
            context.ack()
        }

        app.blockAction(CreationConstants.ActionID.POLL_TYPE) { request, context ->
            provider.client(context.asyncClient())
            /*
            val action = request.payload.actions.first()
            val pollType = PollType.valueOf(action.selectedOption.value)
            */
            context.ack()
        }
    }

    companion object {
        val GSON = CreationConstants.GSON
    }
}
