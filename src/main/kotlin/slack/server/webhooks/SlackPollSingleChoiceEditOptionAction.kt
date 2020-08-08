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

class SlackPollSingleChoiceEditOptionAction(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
) : SlackBlockActionCommandWebhook<SlackPollSingleChoiceEditOptionData, CreationMetadata>(
    provider,
    SlackPollSingleChoiceEditOptionData.Companion,
    CreationMetadata::class.java
) {
    override val actionID: String = CreationConstant.ActionID.SINGLE_POLL_EDIT_CHOICE

    override fun handle(metadata: CreationMetadata, content: SlackPollSingleChoiceEditOptionData) {
        val pollBuilder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()

        pollBuilder.apply {
            options = if (options.isEmpty()) {
                options + PollOption(UUID.randomUUID().toString(), "")
            } else {
                options
            }
        }
        val addView = EditOptionsPollView(
            metadata,
            pollBuilder.options
        )

        provider.pushView(addView, content.triggerID)
        creationRepository.put(metadata.pollID, pollBuilder)
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
