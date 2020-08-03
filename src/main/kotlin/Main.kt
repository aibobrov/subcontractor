import com.slack.api.bolt.App
import com.slack.api.bolt.jetty.SlackAppServer

import core.model.PollType
import slack.model.SlackConversation
import slack.model.SlackUser
import slack.request.SlackRequestManagerProvider
import slack.request.SlackRequestManagerProviderImpl
import slack.ui.create.AddOptionsPollView
import slack.ui.create.CreatePollView
import java.util.concurrent.CompletableFuture

private fun combine(
    users: CompletableFuture<List<SlackUser>>,
    channels: CompletableFuture<List<SlackConversation>>
): Pair<List<SlackUser>, List<SlackConversation>> {
    return users
        .thenCombine(channels) { usersList, channelsList ->
            usersList to channelsList
        }
        .get()
}

val provider: SlackRequestManagerProvider = SlackRequestManagerProviderImpl()
fun main() {
    val app = App()

    app.command("/liquid") { request, context ->
        provider.client(context.asyncClient())
        val (users, channels) = combine(
            provider.usersList(),
            provider.conversationsList()
        )

        val tmp = CreatePollView(
            PollType.SINGLE_CHOICE,
            MOCK.OPTIONS,
            users,
            channels
        )

        provider.openView(tmp, context.triggerId)

        context.ack()
    }

    app.blockAction("single-option-poll-add") { req, context ->
        context.client().viewsPush { requestBuilder ->
            val tmp = AddOptionsPollView(MOCK.OPTIONS.map { it.content.toString() })

            requestBuilder
                .view(tmp.representation())
                .triggerId(context.triggerId)
        }
        context.ack()
    }

    // don't forget to run `ngrok http 3000` and change urls in slack api
    val server = SlackAppServer(app) // starts server at 3000 port
    server.start()
}
