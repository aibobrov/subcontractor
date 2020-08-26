package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import core.model.base.Poll
import core.model.base.PollTag
import slack.model.SlackPollMetadata
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.SlackViewBlockActionWebhook
import slack.server.base.SlackViewSubmissionDataFactory
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant

class SlackPollCreationTagPickerAction(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
) : SlackViewBlockActionWebhook<SlackPollCreationTagPickerData, SlackPollMetadata>(
    provider,
    SlackPollCreationTagPickerData.Companion,
    SlackPollMetadata::class.java
) {
    override val actionID: String = UIConstant.ActionID.POLL_TAG

    override fun handle(metadata: SlackPollMetadata, content: SlackPollCreationTagPickerData) {
        val builder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()
        builder.apply { tags = content.pollTags }
    }
}

data class SlackPollCreationTagPickerData(val pollTags: List<PollTag>) {
    companion object : SlackBlockActionDataFactory<SlackPollCreationTagPickerData> {
        override fun fromRequest(request: BlockActionRequest, context: ActionContext): SlackPollCreationTagPickerData {
            val action = request.payload.actions.first()
            val pollTags = action.selectedOptions.map { PollTag.valueOf(it.value) }
            return SlackPollCreationTagPickerData(pollTags)
        }
    }
}