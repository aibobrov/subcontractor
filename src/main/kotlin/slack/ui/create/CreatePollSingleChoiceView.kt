package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.OverflowMenuElementBuilder
import com.slack.api.model.kotlin_extension.block.element.StaticSelectElementBuilder
import core.model.PollOption
import core.model.PollType
import slack.model.OptionAction
import slack.ui.base.SlackBlockUIRepresentable

class CreatePollSingleChoiceView(private val options: List<PollOption>) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        builder.apply {
            buildPollActionElements(this)
            buildPollOptions(this)
        }
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
                    actionId(CreationConstants.ActionID.OPTION_ACTION_OVERFLOW)
                    buildPollOptionOverflowMenu(this, isLast)
                }
            }
        }
    }

    private fun buildPollActionElements(builder: LayoutBlockDsl) {
        fun buildStaticPollTypeSelect(builder: StaticSelectElementBuilder) {
            builder.options {
                PollType.values().forEach {
                    option {
                        plainText(it.toString())
                        value(it.name)
                    }
                }
            }
        }

        builder.actions {
            elements {
                staticSelect {
                    actionId(CreationConstants.ActionID.POLL_TYPE)
                    buildStaticPollTypeSelect(this)
                    initialOption {
                        value(POLL_TYPE.name)
                        plainText(POLL_TYPE.toString())
                    }
                }
                button {
                    text(ADD_CHOICES_BUTTON_TITLE)
                    actionId(CreationConstants.ActionID.SINGLE_POLL_EDIT_CHOICE)
                }
            }
        }
    }


    companion object {
        private const val OVERFLOW_MOVE_LABEL = "⬇️  Move option down"
        private const val OVERFLOW_DELETE_LABEL = "❌  Delete option"
        private val POLL_TYPE = PollType.SINGLE_CHOICE
        private const val ADD_CHOICES_BUTTON_TITLE = "Edit choices"
    }
}
