package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.ButtonStyle
import com.slack.api.model.kotlin_extension.block.element.dsl.BlockElementDsl
import slack.model.PollAdvancedOption
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class AdvancedSettingsBlockView(
    private val advancedOption: PollAdvancedOption
) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        builder.section {
            markdownText("*$ADVANCED_SETTING_TEXT*")
        }
        builder.actions {
            elements {
                buildShowResponsesOption(this, advancedOption.showResponses)
                buildIsAnonymousOption(this, advancedOption.isAnonymous)
//                buildStartPollDateOption(this, advancedOption.startDateTimeEnabled)
//                buildFinishPollDateOption(this, advancedOption.finishDateTimeEnabled)
            }
        }
    }

    private fun buildStartPollDateOption(builder: BlockElementDsl, flag: Boolean) {
        buildBinaryButton(builder, flag, START_TIME_ENABLE_TEXT, UIConstant.ActionID.START_DATETIME_TOGGLE)
    }

    private fun buildFinishPollDateOption(builder: BlockElementDsl, flag: Boolean) {
        buildBinaryButton(builder, flag, FINISH_TIME_ENABLE_TEXT, UIConstant.ActionID.FINISH_DATETIME_TOGGLE)
    }

    private fun buildShowResponsesOption(builder: BlockElementDsl, flag: Boolean) {
        buildBinaryButton(builder, flag, SHOW_RESPONSES_TEXT, UIConstant.ActionID.SHOW_RESPONSES_TOGGLE)
    }

    private fun buildIsAnonymousOption(builder: BlockElementDsl, flag: Boolean) {
        buildBinaryButton(builder, flag, ANONYMOUS_TEXT, UIConstant.ActionID.ANONYMOUS_TOGGLE)
    }

    private fun buildBinaryButton(builder: BlockElementDsl, flag: Boolean, text: String, actionID: String) {
        builder.button {
            text(titleForButton(text, flag), emoji = flag)
            actionId(actionID)
            if (flag)
                style(ButtonStyle.PRIMARY)
        }
    }


    companion object {
        const val ADVANCED_SETTING_TEXT = "Advanced settings"
        const val START_TIME_ENABLE_TEXT = "Schedule start time"
        const val FINISH_TIME_ENABLE_TEXT = "Schedule finish time"
        const val ANONYMOUS_TEXT = "Anonymous"
        const val SHOW_RESPONSES_TEXT = "Show responses"

        fun titleForButton(text: String, flag: Boolean): String {
            return if (flag) {
                "✅ $text"
            } else {
                text
            }
        }
    }
}
