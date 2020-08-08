package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import slack.model.PollBuilder
import slack.server.base.SlackBlockActionCommandWebhook
import slack.server.base.SlackBlockActionDataFactory
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.create.CreationConstant
import slack.ui.create.CreationMetadata
import java.time.LocalDate
import java.time.LocalDateTime

abstract class SlackPollCreationDatePickerAction(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
) : SlackBlockActionCommandWebhook<SlackPollCreationDatePickerData, CreationMetadata>(
    provider,
    SlackPollCreationDatePickerData.Companion,
    CreationMetadata::class.java
) {
    override fun handle(metadata: CreationMetadata, content: SlackPollCreationDatePickerData) {
        val pollBuilder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()
        updateBuilder(pollBuilder, content)
        // TODO: update view
    }

    abstract fun updateBuilder(builder: PollBuilder, content: SlackPollCreationDatePickerData)
}

class SlackPollCreationStartDatePickerAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackPollCreationDatePickerAction(provider, creationRepository) {

    override val actionID: String = CreationConstant.ActionID.START_DATE_PICKER
    override fun updateBuilder(builder: PollBuilder, content: SlackPollCreationDatePickerData) {
        builder.apply {
            startTime = (startTime ?: LocalDateTime.now())
                .withMonth(content.selectedDate.monthValue)
                .withYear(content.selectedDate.year)
                .withDayOfMonth(content.selectedDate.dayOfMonth)
        }
    }
}

class SlackPollCreationFinishDatePickerAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackPollCreationDatePickerAction(provider, creationRepository) {

    override val actionID: String = CreationConstant.ActionID.FINISH_DATE_PICKER
    override fun updateBuilder(builder: PollBuilder, content: SlackPollCreationDatePickerData) {
        builder.apply {
            finishTime = (finishTime ?: LocalDateTime.now())
                .withMonth(content.selectedDate.monthValue)
                .withYear(content.selectedDate.year)
                .withDayOfMonth(content.selectedDate.dayOfMonth)
        }
    }
}

data class SlackPollCreationDatePickerData(val selectedDate: LocalDate) {
    companion object : SlackBlockActionDataFactory<SlackPollCreationDatePickerData> {
        override fun fromRequest(
            request: BlockActionRequest,
            context: ActionContext
        ): SlackPollCreationDatePickerData {
            val selectedDateString = request.payload.actions.first().selectedDate
            val selectedDate = LocalDate.parse(selectedDateString, CreationConstant.DATE_FORMATTER)
            return SlackPollCreationDatePickerData(selectedDate)
        }
    }
}
