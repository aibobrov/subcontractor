package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import slack.model.SlackPollBuilder
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant
import java.time.LocalDate
import java.time.LocalDateTime

class SlackViewPollCreationStartDatePickerAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackViewPollCreationSettingAction<SlackPollCreationDatePickerData>(
    provider,
    creationRepository,
    SlackPollCreationDatePickerData.Companion
) {
    override val actionID: String = UIConstant.ActionID.START_DATE_PICKER

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationDatePickerData) {
        builder.apply {
            startTime = (startTime ?: LocalDateTime.now())
                .withMonth(content.selectedDate.monthValue)
                .withYear(content.selectedDate.year)
                .withDayOfMonth(content.selectedDate.dayOfMonth)
        }
    }
}

class SlackViewPollCreationFinishDatePickerAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackViewPollCreationSettingAction<SlackPollCreationDatePickerData>(
    provider,
    creationRepository,
    SlackPollCreationDatePickerData.Companion
) {
    override val actionID: String = UIConstant.ActionID.FINISH_DATE_PICKER

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationDatePickerData) {
        builder.apply {
            finishTime = (finishTime ?: LocalDateTime.now())
                .withMonth(content.selectedDate.monthValue)
                .withYear(content.selectedDate.year)
                .withDayOfMonth(content.selectedDate.dayOfMonth)
        }
    }
}

data class SlackPollCreationDatePickerData(override val viewID: String, val selectedDate: LocalDate) :
    ViewIdentifiable {
    companion object : SlackBlockActionDataFactory<SlackPollCreationDatePickerData> {
        override fun fromRequest(
            request: BlockActionRequest,
            context: ActionContext
        ): SlackPollCreationDatePickerData {
            val selectedDateString = request.payload.actions.first().selectedDate
            val selectedDate = LocalDate.parse(selectedDateString, UIConstant.DATE_FORMATTER)
            return SlackPollCreationDatePickerData(request.payload.view.id, selectedDate)
        }
    }
}
