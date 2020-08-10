package slack.server.base

import com.slack.api.bolt.App
import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import slack.service.SlackRequestProvider

abstract class SlackSlashCommandWebhook<Content>(
    protected val provider: SlackRequestProvider,
    private val dataFactory: SlackDataFactory<SlashCommandRequest, SlashCommandContext, Content>
) : SlackWebhook<Content> {

    abstract val commandName: String

    override fun registerIn(app: App) {
        app.command(commandName) { request, context ->
            provider.client(context.asyncClient())
            val data = dataFactory.fromRequest(request, context)
            handle(data)
            context.ack()
        }
    }
}
