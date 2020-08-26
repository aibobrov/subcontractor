package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.PollAudience
import core.model.PollVoter
import slack.model.SlackPollMetadata
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.SlackViewBlockActionWebhook
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant

class SlackPollCreationAudiencePickerAction(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
) : SlackViewBlockActionWebhook<SlackPollCreationAudiencePickerData, SlackPollMetadata>(
    provider,
    SlackPollCreationAudiencePickerData.Companion,
    SlackPollMetadata::class.java
) {
    override val actionID: String = UIConstant.ActionID.POLL_AUDIENCE

    override fun handle(metadata: SlackPollMetadata, content: SlackPollCreationAudiencePickerData) {
        val builder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()
        builder.apply { audience = content.audience }
    }
}

data class SlackPollCreationAudiencePickerData(
    val audience: PollAudience
) {
    companion object : SlackBlockActionDataFactory<SlackPollCreationAudiencePickerData> {
        override fun fromRequest(
            request: BlockActionRequest,
            context: ActionContext
        ): SlackPollCreationAudiencePickerData {
            val action = request.payload.actions.first()
            val selectedConversation = action.selectedConversations.map { PollVoter(it) }
            val audience = PollAudience(selectedConversation)
            return SlackPollCreationAudiencePickerData(audience)
        }
    }
}
