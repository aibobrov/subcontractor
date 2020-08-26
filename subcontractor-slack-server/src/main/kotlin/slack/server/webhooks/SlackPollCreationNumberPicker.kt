package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import slack.model.SlackPollBuilder
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant

class SlackPollCreationNumberPickerAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackViewPollCreationSettingAction<SlackPollCreationNumberPickerData>(
    provider,
    creationRepository,
    SlackPollCreationNumberPickerData.Companion
) {
    override val actionID: String = UIConstant.ActionID.POLL_NUMBER

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationNumberPickerData) {
        builder.apply { number = content.number }
    }
}

data class SlackPollCreationNumberPickerData(
    override val viewID: String,
    val number: Int
) : ViewIdentifiable {
    companion object : SlackBlockActionDataFactory<SlackPollCreationNumberPickerData> {
        override fun fromRequest(
            request: BlockActionRequest,
            context: ActionContext
        ): SlackPollCreationNumberPickerData {
            val action = request.payload.actions.first()
            val number = action.selectedOption.value.toInt()
            return SlackPollCreationNumberPickerData(
                request.payload.view.id,
                number
            )
        }
    }
}
