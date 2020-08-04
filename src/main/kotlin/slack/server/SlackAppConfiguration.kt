package slack.server

import com.slack.api.bolt.App
import combine
import core.model.PollAuthor
import core.model.PollType
import core.model.SingleChoicePoll
import core.model.base.VotingTime
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.create.AddOptionsPollView
import slack.ui.create.CreatePollView
import slack.ui.create.CreationIDConstants


@Configuration
open class SlackAppConfiguration(
    private val provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
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

            val view = CreatePollView(
                PollType.DEFAULT,
                listOf(),
                users,
                channels
            )

            provider.openView(view, context.triggerId)

            context.ack()
        }

        app.viewSubmission(CreationIDConstants.CREATION_VIEW_SUBMISSION_CALLBACK) { request, context ->
            provider.client(context.asyncClient())
            /*
            val state = request.payload.view.state
            val question = state.let {
                it.values[CreationIDConstants.QUESTION_BLOCK_ID]?.get(CreationIDConstants.POLL_QUESTION)?.value
            }

            val audience =
                state.values[CreationIDConstants.AUDIENCE_BLOCK_ID]?.get(CreationIDConstants.POLL_AUDIENCE)?.selectedOptions
            */
            context.ack()
        }

        app.viewSubmission(CreationIDConstants.ADD_OPTION_VIEW_SUBMISSION_CALLBACK) { request, context ->
            provider.client(context.asyncClient())
            /*
            val state = request.payload.view.state
            val options = state.values.values.flatMap { id2StateMap ->
                id2StateMap.entries.map {
                    val id = it.key
                    val content = it.value.value
                    PollOption(id, content)
                }
            }
            */
            context.ack()
        }

        app.blockAction(CreationIDConstants.SINGLE_POLL_ADD_CHOICE) { request, context ->
            provider.client(context.asyncClient())
            val addView = AddOptionsPollView(MOCK.OPTIONS)
            provider.pushView(addView, context.triggerId)
            context.ack()
        }

        app.blockAction(CreationIDConstants.POLL_TYPE) { request, context ->
            provider.client(context.asyncClient())
            /*
            val action = request.payload.actions.first()
            val pollType = PollType.valueOf(action.selectedOption.value)
            */
            context.ack()
        }
    }
}
