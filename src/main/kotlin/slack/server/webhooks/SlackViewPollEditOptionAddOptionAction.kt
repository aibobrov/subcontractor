package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.PollOption
import slack.model.SlackUIFactory
import slack.server.base.SlackViewBlockActionWebhook
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.model.SlackPollMetadata
import slack.ui.base.UIConstant
import java.util.*

class SlackViewPollEditOptionAddOptionAction(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
) : SlackViewBlockActionWebhook<SlackPollEditOptionAddOptionData, SlackPollMetadata>(
    provider,
    SlackPollEditOptionAddOptionData.Companion,
    SlackPollMetadata::class.java
) {
    override val actionID: String = UIConstant.ActionID.ADD_NEW_OPTION_BUTTON

    override fun handle(metadata: SlackPollMetadata, content: SlackPollEditOptionAddOptionData) {
        val builder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()

        builder.apply {
            options = builder.options + PollOption(UUID.randomUUID().toString(), "")
        }

        val view = SlackUIFactory.editOptionsView(
            metadata,
            builder
        )

        provider.updateView(view, content.viewID)
    }
}

data class SlackPollEditOptionAddOptionData(
    override val viewID: String
): ViewIdentifiable {
    companion object : SlackBlockActionDataFactory<SlackPollEditOptionAddOptionData> {
        override fun fromRequest(
            request: BlockActionRequest,
            context: ActionContext
        ): SlackPollEditOptionAddOptionData {
            return SlackPollEditOptionAddOptionData(request.payload.view.id)
        }
    }
}
