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
    private val currentDate: LocalDateTime,
    private val initialTime: LocalTime? = null,
    private val initialDate: LocalDate = LocalDate.now()
) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        val timeSelection = timeOptions(currentDate)
        val times = if (timeSelection.isEmpty()) { // can be empty. Example: currentDate = 23:50
            listOf(currentDate.toLocalTime())
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
            initialDate(initialDate.format(CreationConstants.DATE_FORMATTER))
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
                        val formatted = time.format(CreationConstants.TIME_FORMATTER)
                        plainText(formatted)
                        value(formatted)
                    }
                }
            }
            initialOption {
                val formatted = (initialTime ?: times.first()).format(CreationConstants.TIME_FORMATTER)
                plainText(formatted)
                value(formatted)
            }
        }
    }

    companion object {
        const val DATE_PLACEHOLDER = "Select date"
        private const val TIME_INTERVAL: Long = 15

        fun timeOptions(forDate: LocalDateTime): List<LocalTime> {
            return if (LocalDateTime.now().toLocalDate().isEqual(forDate.toLocalDate())) {
                DateUtils.timesBy(TIME_INTERVAL, forDate.toLocalTime())
            } else {
                DateUtils.timesBy(TIME_INTERVAL)
            }
        }
    }
}


class StartDateTimePickerBlockView(
    currentDate: LocalDateTime,
    initialTime: LocalTime? = null,
    initialDate: LocalDate = LocalDate.now()
) : DateTimePickerBlockView(
    "Send At",
    CreationConstants.ActionID.START_DATE_PICKER,
    CreationConstants.ActionID.START_TIME_PICKER,
    currentDate,
    initialTime,
    initialDate
)

class FinishDateTimeBlockView(
    currentDate: LocalDateTime,
    initialTime: LocalTime? = null,
    initialDate: LocalDate = LocalDate.now()
) : DateTimePickerBlockView(
    "Finish At",
    CreationConstants.ActionID.FINISH_DATE_PICKER,
    CreationConstants.ActionID.FINISH_TIME_PICKER,
    currentDate,
    initialTime,
    initialDate
)
