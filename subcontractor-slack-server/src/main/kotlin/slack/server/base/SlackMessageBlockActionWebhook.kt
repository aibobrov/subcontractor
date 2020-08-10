package slack.server.base

import com.slack.api.bolt.App
import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import slack.service.SlackRequestProvider

abstract class SlackMessageBlockActionWebhook<Content>(
    protected val provider: SlackRequestProvider,
    private val dataFactory: SlackDataFactory<BlockActionRequest, ActionContext, Content>
) : SlackWebhook<Content> {
    abstract val actionID: String
    override fun registerIn(app: App) {
        app.blockAction(actionID) { request, context ->
            provider.client(context.asyncClient())
            val data = dataFactory.fromRequest(request, context)
            handle(data)
            context.ack()
        }
    }
}
