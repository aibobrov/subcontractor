package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import slack.model.OptionAction
import slack.model.SlackValidator
import slack.model.SlackUIFactory
import slack.server.base.SlackViewBlockActionWebhook
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant
import slack.model.SlackPollMetadata
import utils.swap

class SlackViewPollCreationSingleChoicePollOverflowAction(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
) : SlackViewBlockActionWebhook<SlackPollCreationSingleChoicePollOverflowData, SlackPollMetadata>(
    provider,
    SlackPollCreationSingleChoicePollOverflowData.Companion,
    SlackPollMetadata::class.java
) {
    override val actionID: String = UIConstant.ActionID.OPTION_ACTION_OVERFLOW

    override fun handle(metadata: SlackPollMetadata, content: SlackPollCreationSingleChoicePollOverflowData) {
        val builder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()
        val optionIndex = builder.options.indexOfFirst { it.id == content.optionID }

        val newOptions = builder.options.toMutableList()
        when (content.optionAction) {
            OptionAction.DELETE -> newOptions.removeAt(optionIndex)
            OptionAction.MOVE_DOWN -> newOptions.swap(optionIndex, optionIndex + 1)
        }
        builder.apply { options = newOptions }

        val errors = SlackValidator.validate(builder)
        val view = SlackUIFactory.creationView(metadata, builder, errors)
        provider.updateView(view, content.viewID)
    }
}


data class SlackPollCreationSingleChoicePollOverflowData(
    override val viewID: String,
    val optionAction: OptionAction,
    val optionID: String
) : ViewIdentifiable {
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
