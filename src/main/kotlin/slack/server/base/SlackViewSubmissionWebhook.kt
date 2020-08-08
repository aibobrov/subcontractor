package slack.server.base

import com.slack.api.bolt.App
import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import slack.service.SlackRequestProvider

abstract class SlackViewSubmissionWebhook<Content, Metadata>(
    protected val provider: SlackRequestProvider,
    private val dataFactory: SlackDataFactory<ViewSubmissionRequest, ViewSubmissionContext, Content>,
    private val classOfMetadata: Class<Metadata>
) : SlackMetadataWebhook<Content, Metadata> {
    abstract val callbackID: String

    override fun registerIn(app: App) {
        app.viewSubmission(callbackID) { request, context ->
            provider.client(context.asyncClient())
            val data = dataFactory.fromRequest(request, context)
            val metadata = Constant.GSON.fromJson(request.payload.view.privateMetadata, classOfMetadata)
            handle(metadata, data)
            context.ack()
        }
    }
}
