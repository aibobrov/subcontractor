package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.SlackPatternBlockActionWebhook
import slack.service.SlackRequestProvider
import slack.model.SlackPollMetadata
import java.util.regex.Pattern

class SlackEmptyAction(
    override val actionID: Pattern,
    provider: SlackRequestProvider
) : SlackPatternBlockActionWebhook<BlankData>(provider, BlankData) {
    override fun handle(content: BlankData) = Unit
}


object BlankData : SlackBlockActionDataFactory<BlankData> {
    override fun fromRequest(request: BlockActionRequest, context: ActionContext): BlankData {
        return BlankData
    }
}
