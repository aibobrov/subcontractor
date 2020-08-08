package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import slack.model.OptionAction
import slack.server.base.SlackBlockActionCommandWebhook
import slack.server.base.SlackBlockActionDataFactory
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.create.CreatePollView
import slack.ui.create.CreationConstant
import slack.ui.create.CreationMetadata
import utils.combine
import utils.swap

class SlackPollCreationSingleChoicePollOverflowAction(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
) : SlackBlockActionCommandWebhook<SlackPollCreationSingleChoicePollOverflowData, CreationMetadata>(
    provider,
    SlackPollCreationSingleChoicePollOverflowData.Companion,
    CreationMetadata::class.java
) {
    override val actionID: String = CreationConstant.ActionID.OPTION_ACTION_OVERFLOW

    override fun handle(metadata: CreationMetadata, content: SlackPollCreationSingleChoicePollOverflowData) {
        val pollBuilder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()
        val optionIndex = pollBuilder.options.indexOfFirst { it.id == content.optionID }

        val newOptions = pollBuilder.options.toMutableList()
        when (content.optionAction) {
            OptionAction.DELETE -> newOptions.removeAt(optionIndex)
            OptionAction.MOVE_DOWN -> newOptions.swap(optionIndex, optionIndex + 1)
        }
        pollBuilder.apply { options = newOptions }

        val audienceFuture = provider.usersList().combine(provider.conversationsList())
        audienceFuture.thenAccept {
            val (users, channels) = it
            val view = CreatePollView(
                metadata,
                pollBuilder.advancedOption,
                pollBuilder.type,
                pollBuilder.options,
                pollBuilder.startTime,
                pollBuilder.finishTime,
                users,
                channels
            )
            provider.updateView(view, content.viewID)
        }
    }
}


data class SlackPollCreationSingleChoicePollOverflowData(
    val viewID: String,
    val optionAction: OptionAction,
    val optionID: String
) {
    companion object : SlackBlockActionDataFactory<SlackPollCreationSingleChoicePollOverflowData> {
        override fun fromRequest(
            request: BlockActionRequest,
            context: ActionContext
        ): SlackPollCreationSingleChoicePollOverflowData {
            val action = request.payload.actions.first()
            val optionAction = OptionAction.valueOf(action.selectedOption.value)
            return SlackPollCreationSingleChoicePollOverflowData(
                request.payload.view.id,
                optionAction,
                action.blockId
            )
        }
    }
}
