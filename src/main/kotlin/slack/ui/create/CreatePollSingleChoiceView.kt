package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.OverflowMenuElementBuilder
import com.slack.api.model.kotlin_extension.block.element.StaticSelectElementBuilder
import core.model.PollOption
import core.model.PollType
import slack.ui.base.SlackBlockUIRepresentable

class CreatePollSingleChoiceView(private val options: List<PollOption>) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        builder.apply {
            buildPollActionElements(this)
            buildPollOptions(this)
        }
    }

    private fun buildPollOptions(builder: LayoutBlockDsl) {
        options.forEach { buildPollOption(builder, it) }
    }

    private fun buildPollOption(builder: LayoutBlockDsl, option: PollOption) {
        fun buildPollOptionOverflowMenu(builder: OverflowMenuElementBuilder) {
            builder.options {
                option {
                    plainText(OVERFLOW_DELETE_LABEL, emoji = true)
                }
                option {
                    plainText(OVERFLOW_EDIT_LABEL, emoji = true)
                }
                option {
                    plainText(OVERFLOW_MOVE_LABEL, emoji = true)
                }
            }
        }
        builder.section {
            plainText(option.content)
            accessory {
                overflowMenu {
                    buildPollOptionOverflowMenu(this)
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
                    actionId(CreationConstants.ActionID.SINGLE_POLL_ADD_CHOICE)
                }
            }
        }
    }


    companion object {
        private const val OVERFLOW_MOVE_LABEL = "⬇️  Move option down"
        private const val OVERFLOW_EDIT_LABEL = "📝  Edit option"
        private const val OVERFLOW_DELETE_LABEL = "❌  Delete option"
        private val POLL_TYPE = PollType.SINGLE_CHOICE
        private const val ADD_CHOICES_BUTTON_TITLE = "Add choices"
    }
}
