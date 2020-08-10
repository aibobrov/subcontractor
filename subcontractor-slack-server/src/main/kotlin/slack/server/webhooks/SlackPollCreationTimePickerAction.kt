package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import slack.model.SlackPollBuilder
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant
import java.time.LocalDateTime
import java.time.LocalTime

data class SlackPollCreationTimePickerData(override val viewID: String, val selectedTime: LocalTime) :
    ViewIdentifiable {
    companion object : SlackBlockActionDataFactory<SlackPollCreationTimePickerData> {
        override fun fromRequest(request: BlockActionRequest, context: ActionContext): SlackPollCreationTimePickerData {
            val selectedTimeString = request.payload.actions.first().selectedOption.value
            val selectedTime = LocalTime.parse(selectedTimeString, UIConstant.TIME_VALUE_FORMATTER)
            return SlackPollCreationTimePickerData(request.payload.view.id, selectedTime)
        }
    }
}

class SlackViewPollCreationStartTimePickerAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackViewPollCreationSettingAction<SlackPollCreationTimePickerData>(
    provider,
    creationRepository,
    SlackPollCreationTimePickerData.Companion
) {
    override val actionID: String = UIConstant.ActionID.START_TIME_PICKER

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationTimePickerData) {
        builder.apply {
            startTime = (startTime ?: LocalDateTime.now())
                .withHour(content.selectedTime.hour)
                .withMinute(content.selectedTime.minute)
                .withSecond(0)
        }
    }
}


class SlackViewPollCreationFinishTimePickerAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackViewPollCreationSettingAction<SlackPollCreationTimePickerData>(
    provider,
    creationRepository,
    SlackPollCreationTimePickerData.Companion
) {
    override val actionID: String = UIConstant.ActionID.FINISH_TIME_PICKER

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationTimePickerData) {
        builder.apply {
            finishTime = (finishTime ?: LocalDateTime.now())
                .withHour(content.selectedTime.hour)
                .withMinute(content.selectedTime.minute)
                .withSecond(0)
        }
    }
}
