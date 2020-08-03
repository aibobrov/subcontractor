package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import slack.ui.base.SlackBlockUIRepresentable

class AddOptionsPollBlockView(private val choices: List<String>) : SlackBlockUIRepresentable {
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
                    text(ADD_OPTION_TITLE_BUTTON)
                }
            }
        }
    }

    private fun buildOptions(builder: LayoutBlockDsl, choices: List<String>) {
        choices.forEachIndexed { offset, content ->
            buildOptionInput(builder, offset + 1, content)
        }
    }

    private fun buildOptionInput(builder: LayoutBlockDsl, number: Int, content: String) {
        builder.input {
            label(optionLabelTitle(number))
            plainTextInput {
                multiline(false)
                initialValue(content)
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
