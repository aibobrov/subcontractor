package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.PollOption
import core.model.PollType
import slack.ui.base.SlackBlockUIRepresentable

class CreatePollBlockView(
    pollType: PollType,
    options: List<PollOption>
) : SlackBlockUIRepresentable {
    private val pollSetupView = createPollSetupView(pollType, options)

    override fun representIn(builder: LayoutBlockDsl) {
        builder.apply {
            buildHeader(this)
            divider()
            buildQuestionInput(this)
            pollSetupView.representIn(this)
        }
    }

    private fun buildHeader(builder: LayoutBlockDsl) {
        builder.section {
            plainText(TITLE_INSTRUCTION)
        }
    }

    private fun buildQuestionInput(builder: LayoutBlockDsl) {
        builder.input {
            blockId(CreationIDConstants.QUESTION_BLOCK_ID)
            label(QUESTION_SECTION_HEADER_TITLE)
            element {
                plainTextInput {
                    actionId(CreationIDConstants.POLL_QUESTION)
                    multiline(false)
                    placeholder(QUESTION_INPUT_PLACEHOLDER)
                }
            }
        }
    }

    companion object {
        private const val TITLE_INSTRUCTION = "Configure and schedule your poll using controls below."
        private const val QUESTION_SECTION_HEADER_TITLE = "Question"
        private const val QUESTION_INPUT_PLACEHOLDER = "Write something"

        private fun createPollSetupView(pollType: PollType, options: List<PollOption>): SlackBlockUIRepresentable {
            return when (pollType) {
                PollType.SINGLE_CHOICE -> CreatePollSingleChoiceView(options)
                PollType.AGREE_DISAGREE -> TODO()
            }
        }
    }
}
