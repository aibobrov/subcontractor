package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.StaticSelectElementBuilder
import core.model.PollOption
import core.model.PollType
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant
import slack.ui.components.PollOptionsBlockView

class CreatePollEditableOptionsBlockView(options: List<PollOption>) : SlackBlockUIRepresentable {
    private val optionsBlockView = PollOptionsBlockView(options)

    override fun representIn(builder: LayoutBlockDsl) {
        builder.apply {
            buildPollActionElements(this)
            optionsBlockView.representIn(this)
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
                    actionId(UIConstant.ActionID.POLL_TYPE)
                    buildStaticPollTypeSelect(this)
                    initialOption {
                        value(POLL_TYPE.name)
                        plainText(POLL_TYPE.toString())
                    }
                }
                button {
                    text(ADD_CHOICES_BUTTON_TITLE)
                    actionId(UIConstant.ActionID.SINGLE_POLL_EDIT_CHOICE)
                }
            }
        }
    }

    companion object {
        private val POLL_TYPE = PollType.SINGLE_CHOICE
        private const val ADD_CHOICES_BUTTON_TITLE = "Edit choices"
    }
}
