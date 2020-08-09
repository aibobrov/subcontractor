package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.dsl.BlockElementDsl
import slack.server.base.Constant
import slack.ui.base.SlackBlockUIRepresentable
import utils.DateUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


abstract class DateTimePickerBlockView(
    private val selectedDateTime: LocalDateTime? // one of the available times(15 min interval)
) : SlackBlockUIRepresentable {
    abstract val dateActionID: String
    abstract val timeActionID: String
    abstract val labelText: String

    override fun representIn(builder: LayoutBlockDsl) {
        val timeSelection = timeOptions(selectedDateTime?.toLocalDate())
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
                        selectedTime(selectedDate, selectedDateTime?.toLocalTime()),
                        timeSelection
                    )
                }
            }
        }
    }

    private fun buildDatePicker(builder: BlockElementDsl, selectedDate: LocalDate) {
        builder.datePicker {
            initialDate(selectedDate.format(CreationConstant.DATE_FORMATTER))
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
                        val formatted = time.format(CreationConstant.TIME_FORMATTER)
                        plainText(formatted)
                        value(formatted)
                    }
                }
            }
            initialOption {
                val time = selectedTime?.let { DateUtils.round(it, Constant.TIME_INTERVAL) } ?: times.first()
                val formatted = time.format(CreationConstant.TIME_FORMATTER)
                plainText(formatted)
                value(formatted)
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
                DateUtils.timesBy(Constant.TIME_INTERVAL, DateUtils.round(LocalTime.now(), Constant.TIME_INTERVAL))
            } else {
                DateUtils.timesBy(Constant.TIME_INTERVAL)
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
    }
}

class StartDateTimePickerBlockView(selectedDateTime: LocalDateTime?) : DateTimePickerBlockView(selectedDateTime) {
    override val dateActionID: String = CreationConstant.ActionID.START_DATE_PICKER

    override val timeActionID: String = CreationConstant.ActionID.START_TIME_PICKER

    override val labelText: String = "Send At"
}

class FinishDateTimePickerBlockView(selectedDateTime: LocalDateTime?) : DateTimePickerBlockView(selectedDateTime) {
    override val dateActionID: String = CreationConstant.ActionID.FINISH_DATE_PICKER

    override val timeActionID: String = CreationConstant.ActionID.FINISH_TIME_PICKER

    override val labelText: String = "Finish At"
}
