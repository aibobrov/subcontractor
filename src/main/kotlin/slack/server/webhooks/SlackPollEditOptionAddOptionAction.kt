package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.PollOption
import slack.model.ViewFactory
import slack.server.base.SlackBlockActionCommandWebhook
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.create.CreationMetadata
import slack.ui.create.CreationConstant
import java.util.*

class SlackPollEditOptionAddOptionAction(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
) : SlackBlockActionCommandWebhook<SlackPollEditOptionAddOptionData, CreationMetadata>(
    provider,
    SlackPollEditOptionAddOptionData.Companion,
    CreationMetadata::class.java
) {
    override val actionID: String = CreationConstant.ActionID.ADD_NEW_OPTION_BUTTON

    override fun handle(metadata: CreationMetadata, content: SlackPollEditOptionAddOptionData) {
        val builder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()

        builder.apply {
            options = builder.options + PollOption(UUID.randomUUID().toString(), "")
        }

        val view = ViewFactory.editOptionsView(
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
