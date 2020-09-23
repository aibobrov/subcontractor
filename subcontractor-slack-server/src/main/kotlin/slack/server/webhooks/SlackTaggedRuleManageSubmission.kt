package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import slack.model.SlackManageMetadata
import slack.server.base.SlackViewSubmissionDataFactory
import slack.server.base.SlackViewSubmissionWebhook
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant

class SlackTaggedRuleManageViewSubmission(
    provider: SlackRequestProvider,
) : SlackViewSubmissionWebhook<SlackTaggedRuleManageViewSubmissionData, SlackManageMetadata>(
    provider,
    SlackTaggedRuleManageViewSubmissionData,
    SlackManageMetadata::class.java
) {
    override val callbackID: String = UIConstant.CallbackID.TAGGED_VIEW_SUBMISSION

    override fun handle(metadata: SlackManageMetadata, content: SlackTaggedRuleManageViewSubmissionData) = Unit
}


object SlackTaggedRuleManageViewSubmissionData :
    SlackViewSubmissionDataFactory<SlackTaggedRuleManageViewSubmissionData> {
    override fun fromRequest(
        request: ViewSubmissionRequest,
        context: ViewSubmissionContext
    ): SlackTaggedRuleManageViewSubmissionData {
        return SlackTaggedRuleManageViewSubmissionData
    }
}
