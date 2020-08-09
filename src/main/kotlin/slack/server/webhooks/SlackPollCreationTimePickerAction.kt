package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import slack.model.SlackPollBuilder
import slack.model.SlackPollBuilderValidator
import slack.model.ViewFactory
import slack.server.base.SlackBlockActionCommandWebhook
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.create.CreationConstant
import slack.ui.create.CreationMetadata
import java.time.LocalDateTime
import java.time.LocalTime

data class SlackPollCreationTimePickerData(override val viewID: String, val selectedTime: LocalTime) :
    ViewIdentifiable {
    companion object : SlackBlockActionDataFactory<SlackPollCreationTimePickerData> {
        override fun fromRequest(request: BlockActionRequest, context: ActionContext): SlackPollCreationTimePickerData {
            val selectedTimeString = request.payload.actions.first().selectedOption.value
            val selectedTime = LocalTime.parse(selectedTimeString, CreationConstant.TIME_FORMATTER)
            return SlackPollCreationTimePickerData(request.payload.view.id, selectedTime)
        }
    }
}

class SlackPollCreationStartTimePickerAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackPollCreationSettingAction<SlackPollCreationTimePickerData>(
    provider,
    creationRepository,
    SlackPollCreationTimePickerData.Companion
) {
    override val actionID: String = CreationConstant.ActionID.START_TIME_PICKER

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationTimePickerData) {
        builder.apply {
            startTime = (startTime ?: LocalDateTime.now())
                .withHour(content.selectedTime.hour)
                .withMinute(content.selectedTime.minute)
                .withSecond(0)
        }
    }
}


class SlackPollCreationFinishTimePickerAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackPollCreationSettingAction<SlackPollCreationTimePickerData>(
    provider,
    creationRepository,
    SlackPollCreationTimePickerData.Companion
) {
    override val actionID: String = CreationConstant.ActionID.FINISH_TIME_PICKER

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationTimePickerData) {
        builder.apply {
            finishTime = (finishTime ?: LocalDateTime.now())
                .withHour(content.selectedTime.hour)
                .withMinute(content.selectedTime.minute)
                .withSecond(0)
        }
    }
}
