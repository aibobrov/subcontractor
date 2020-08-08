package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.PollOption
import slack.server.base.SlackBlockActionCommandWebhook
import slack.server.base.SlackBlockActionDataFactory
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.create.CreationMetadata
import slack.ui.create.CreationConstant
import slack.ui.create.EditOptionsPollView
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
        val pollBuilder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()

        pollBuilder.apply {
            options = pollBuilder.options + PollOption(UUID.randomUUID().toString(), "")
        }

        val addView = EditOptionsPollView(
            metadata,
            pollBuilder.options
        )

        provider.updateView(addView, content.viewID)
    }
}

data class SlackPollEditOptionAddOptionData(
    val viewID: String
) {
    companion object : SlackBlockActionDataFactory<SlackPollEditOptionAddOptionData> {
        override fun fromRequest(
            request: BlockActionRequest,
            context: ActionContext
        ): SlackPollEditOptionAddOptionData {
            return SlackPollEditOptionAddOptionData(request.payload.view.id)
        }
    }
}
