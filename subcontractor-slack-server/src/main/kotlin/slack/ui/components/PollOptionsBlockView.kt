package slack.ui.components

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.OverflowMenuElementBuilder
import core.model.PollOption
import slack.model.OptionAction
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class PollOptionsBlockView(private val options: List<PollOption>) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        buildPollOptions(builder)
    }

    private fun buildPollOptions(builder: LayoutBlockDsl) {
        options.forEachIndexed { index, option ->
            buildPollOption(builder, option, index == options.size - 1)
        }
    }

    private fun buildPollOption(builder: LayoutBlockDsl, option: PollOption, isLast: Boolean) {
        fun buildPollOptionOverflowMenu(builder: OverflowMenuElementBuilder, isLast: Boolean) {
            builder.options {
                option {
                    value(OptionAction.DELETE.name)
                    plainText(OVERFLOW_DELETE_LABEL, emoji = true)
                }
                if (!isLast) {
                    option {
                        value(OptionAction.MOVE_DOWN.name)
                        plainText(OVERFLOW_MOVE_LABEL, emoji = true)
                    }
                }
            }
        }
        builder.section {
            blockId(option.id)
            plainText(option.content)
            accessory {
                overflowMenu {
                    actionId(UIConstant.ActionID.OPTION_ACTION_OVERFLOW)
                    buildPollOptionOverflowMenu(this, isLast)
                }
            }
        }
    }

    companion object {
        private const val OVERFLOW_MOVE_LABEL = "⬇️  Move option down"
        private const val OVERFLOW_DELETE_LABEL = "❌  Delete option"
    }
}
