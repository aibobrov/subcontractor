package slack.server

import com.slack.api.bolt.App
import combine
import core.model.PollType
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
        app.command("/liquid") { _, context ->
            provider.client(context.asyncClient())

            val audienceFuture = provider.usersList().combine(provider.conversationsList())
            val (users, channels) = audienceFuture.get()

            val view = CreatePollView(
                PollType.DEFAULT,
                MOCK.OPTIONS,
                users,
                channels
            )

            provider.openView(view, context.triggerId)

            context.ack()
        }

        app.viewSubmission(CreationIDConstants.CREATION_VIEW_CALLBACK) { request, context ->
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

        app.blockAction(CreationIDConstants.SINGLE_POLL_ADD_CHOICE) { request, context ->
            provider.client(context.asyncClient())
            val addView = AddOptionsPollView(MOCK.OPTIONS.map { it.content })
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
