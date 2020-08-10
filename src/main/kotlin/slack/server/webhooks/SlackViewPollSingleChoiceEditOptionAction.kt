package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.PollOption
import slack.model.SlackUIFactory
import slack.server.base.SlackViewBlockActionWebhook
import slack.server.base.SlackBlockActionDataFactory
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant
import slack.model.SlackPollMetadata
import java.util.*

class SlackViewPollSingleChoiceEditOptionAction(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
) : SlackViewBlockActionWebhook<SlackPollSingleChoiceEditOptionData, SlackPollMetadata>(
    provider,
    SlackPollSingleChoiceEditOptionData.Companion,
    SlackPollMetadata::class.java
) {
    override val actionID: String = UIConstant.ActionID.SINGLE_POLL_EDIT_CHOICE

    override fun handle(metadata: SlackPollMetadata, content: SlackPollSingleChoiceEditOptionData) {
        val builder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()

        builder.apply {
            options = if (options.isEmpty()) {
                options + PollOption(UUID.randomUUID().toString(), "")
            } else {
                options
            }
        }

        val addView = SlackUIFactory.editOptionsView(metadata, builder)

        provider.pushView(addView, content.triggerID)
    }
}

class SlackPollSingleChoiceEditOptionData(
    val triggerID: String
) {
    companion object : SlackBlockActionDataFactory<SlackPollSingleChoiceEditOptionData> {
        override fun fromRequest(
            request: BlockActionRequest,
            context: ActionContext
        ): SlackPollSingleChoiceEditOptionData {
            return SlackPollSingleChoiceEditOptionData(
                context.triggerId
            )
        }
    }
}
