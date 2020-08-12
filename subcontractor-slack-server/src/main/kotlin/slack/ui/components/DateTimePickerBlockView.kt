package slack.ui.components

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.dsl.BlockElementDsl
import slack.server.base.SlackConstant
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant
import utils.DateUtils
import utils.unixEpochTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


abstract class DateTimePickerBlockView(
    private val selectedDateTime: LocalDateTime? // one of the available times(15 min interval)
) : SlackBlockUIRepresentable {
    protected abstract val dateActionID: String
    protected abstract val timeActionID: String
    protected abstract val labelText: String

    override fun representIn(builder: LayoutBlockDsl) {
        val timeSelection =
            timeOptions(selectedDateTime?.toLocalDate())
        builder.apply {
            section {
                markdownText("*$labelText*")
            }
            actions {
                elements {
                    val selectedDate = selectedDateTime?.toLocalDate() ?: LocalDate.now()
                    buildDatePicker(this, DateUtils.max(selectedDate, LocalDate.now()))
                    buildStaticTimeSelect(
                        this,
                        selectedTime(
                            selectedDate,
                            selectedDateTime?.toLocalTime()
                        ),
                        timeSelection
                    )
                }
            }
        }
    }

    private fun buildDatePicker(builder: BlockElementDsl, selectedDate: LocalDate) {
        builder.datePicker {
            initialDate(selectedDate.format(UIConstant.DATE_FORMATTER))
            actionId(dateActionID)
            placeholder(DATE_PLACEHOLDER)
        }
    }

    private fun buildStaticTimeSelect(builder: BlockElementDsl, selectedTime: LocalTime?, times: List<LocalTime>) {
        builder.staticSelect {
            actionId(timeActionID)
            options {
                for (time in times) {
                    option {
                        plainText(formattedTime(time.unixEpochTimestamp))
                        val formattedValue = time.format(UIConstant.TIME_VALUE_FORMATTER)
                        value(formattedValue)
                    }
                }
            }
            initialOption {
                val initialTime = selectedTime?.let { DateUtils.round(it, SlackConstant.TIME_INTERVAL) } ?: times.first()
                plainText(formattedTime(initialTime.unixEpochTimestamp))
                val formattedValue = initialTime.format(UIConstant.TIME_VALUE_FORMATTER)
                value(formattedValue)
            }
        }
    }

    companion object {
        const val DATE_PLACEHOLDER = "Select date"

        // Builds time option for selected date
        // If date == TODAY then time := list(NOW..23:59)
        // ELSE time := list(00:00..23:59)
        fun timeOptions(forDate: LocalDate?): List<LocalTime> {
            val date = forDate ?: LocalDate.now()
            val timeList = if (LocalDateTime.now().toLocalDate().isEqual(date)) {
                DateUtils.timesBy(
                    SlackConstant.TIME_INTERVAL,
                    DateUtils.round(LocalTime.now(), SlackConstant.TIME_INTERVAL)
                )
            } else {
                DateUtils.timesBy(SlackConstant.TIME_INTERVAL)
            }
            return if (timeList.isEmpty()) { // can be empty. Example: currentDate = 23:50
                listOf(LocalTime.now())
            } else {
                timeList
            }
        }

        fun selectedTime(selectedDate: LocalDate, time: LocalTime?): LocalTime? {
            return if (LocalDateTime.now().toLocalDate().isEqual(selectedDate)) {
                time?.let { DateUtils.max(it, LocalTime.now()) }
            } else {
                time
            }
        }

        private fun formattedTime(timestamp: Long): String = "<!date^$timestamp^{time}|00:00>"
    }
}

class StartDateTimePickerBlockView(selectedDateTime: LocalDateTime?) : DateTimePickerBlockView(selectedDateTime) {
    override val dateActionID: String = UIConstant.ActionID.START_DATE_PICKER

    override val timeActionID: String = UIConstant.ActionID.START_TIME_PICKER

    override val labelText: String = "Send At"
}

class FinishDateTimePickerBlockView(selectedDateTime: LocalDateTime?) : DateTimePickerBlockView(selectedDateTime) {
    override val dateActionID: String = UIConstant.ActionID.FINISH_DATE_PICKER

    override val timeActionID: String = UIConstant.ActionID.FINISH_TIME_PICKER

    override val labelText: String = "Finish At"
}
