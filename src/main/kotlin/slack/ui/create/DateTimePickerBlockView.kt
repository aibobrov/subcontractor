package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.dsl.BlockElementDsl
import slack.ui.base.SlackBlockUIRepresentable
import utils.DateUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

open class DateTimePickerBlockView(
    private val labelText: String,
    private val dateActionID: String,
    private val timeActionID: String,
    private val currentDateTime: LocalDateTime,
    private val initialTime: LocalTime? = null,
    private val initialDate: LocalDate = LocalDate.now()
) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        val timeSelection = timeOptions(currentDateTime)
        val times = if (timeSelection.isEmpty()) { // can be empty. Example: currentDate = 23:50
            listOf(currentDateTime.toLocalTime())
        } else {
            timeSelection
        }
        builder.apply {
            section {
                markdownText("*$labelText*")
            }
            actions {
                elements {
                    buildDatePicker(this, initialDate)
                    buildStaticTimeSelect(this, times)
                }
            }
        }
    }

    private fun buildDatePicker(builder: BlockElementDsl, initialDate: LocalDate) {
        builder.datePicker {
            initialDate(initialDate.format(CreationConstant.DATE_FORMATTER))
            actionId(dateActionID)
            placeholder(DATE_PLACEHOLDER)
        }
    }

    private fun buildStaticTimeSelect(builder: BlockElementDsl, times: List<LocalTime>) {
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
                val formatted = (initialTime ?: times.first()).format(CreationConstant.TIME_FORMATTER)
                plainText(formatted)
                value(formatted)
            }
        }
    }

    companion object {
        const val DATE_PLACEHOLDER = "Select date"
        private const val TIME_INTERVAL: Long = 15

        fun timeOptions(fromDate: LocalDateTime): List<LocalTime> {
            return if (LocalDateTime.now().toLocalDate().isEqual(fromDate.toLocalDate())) {
                DateUtils.timesBy(TIME_INTERVAL, fromDate.toLocalTime())
            } else {
                DateUtils.timesBy(TIME_INTERVAL)
            }
        }
    }
}


class StartDateTimePickerBlockView(
    currentDateTime: LocalDateTime,
    initialTime: LocalTime? = null,
    initialDate: LocalDate = LocalDate.now()
) : DateTimePickerBlockView(
    "Send At",
    CreationConstant.ActionID.START_DATE_PICKER,
    CreationConstant.ActionID.START_TIME_PICKER,
    currentDateTime,
    initialTime,
    initialDate
)

class FinishDateTimeBlockView(
    currentDateTime: LocalDateTime,
    initialTime: LocalTime? = null,
    initialDate: LocalDate = LocalDate.now()
) : DateTimePickerBlockView(
    "Finish At",
    CreationConstant.ActionID.FINISH_DATE_PICKER,
    CreationConstant.ActionID.FINISH_TIME_PICKER,
    currentDateTime,
    initialTime,
    initialDate
)
