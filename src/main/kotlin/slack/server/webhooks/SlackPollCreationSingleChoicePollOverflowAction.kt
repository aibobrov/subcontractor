package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import slack.model.OptionAction
import slack.model.SlackPollBuilderValidator
import slack.model.ViewFactory
import slack.server.base.SlackBlockActionCommandWebhook
import slack.server.base.SlackBlockActionDataFactory
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.create.CreationConstant
import slack.ui.create.CreationMetadata
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
        val builder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()
        val optionIndex = builder.options.indexOfFirst { it.id == content.optionID }

        val newOptions = builder.options.toMutableList()
        when (content.optionAction) {
            OptionAction.DELETE -> newOptions.removeAt(optionIndex)
            OptionAction.MOVE_DOWN -> newOptions.swap(optionIndex, optionIndex + 1)
        }
        builder.apply { options = newOptions }

        val audienceFuture = provider.audienceList()
        audienceFuture.thenAccept { audience ->
            val errors = SlackPollBuilderValidator.validate(builder)
            val view = ViewFactory.creationView(metadata, builder, audience, errors)
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
