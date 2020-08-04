package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.PollOption
import slack.ui.base.SlackBlockUIRepresentable

class AddOptionsPollBlockView(private val choices: List<PollOption>) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        builder.apply {
            buildOptions(this, choices)

            buildInfo(this, choices.size)
        }
    }

    private fun buildInfo(builder: LayoutBlockDsl, count: Int) {
        builder.section {
            markdownText(optionLabelInfoTitle(count))
            accessory {
                button {
                    actionId(CreationIDConstants.ADD_NEW_OPTION_BUTTON)
                    text(ADD_OPTION_TITLE_BUTTON)
                }
            }
        }
    }

    private fun buildOptions(builder: LayoutBlockDsl, choices: List<PollOption>) {
        choices.forEachIndexed { offset, content ->
            buildOptionInput(builder, offset + 1, content)
        }
    }

    private fun buildOptionInput(builder: LayoutBlockDsl, number: Int, option: PollOption) {
        builder.input {
            blockId(number.toString())
            label(optionLabelTitle(number))
            plainTextInput {
                multiline(false)
                initialValue(option.content)
                actionId(option.id)
            }
        }
    }

    companion object {
        private fun optionLabelTitle(number: Int): String {
            return "Option $number"
        }

        private fun optionLabelInfoTitle(number: Int): String {
            return if (number == 1) {
                "*$number Option used*"
            } else {
                "*$number Options used*"
            }
        }

        private const val ADD_OPTION_TITLE_BUTTON = "Add another option"
    }
}
