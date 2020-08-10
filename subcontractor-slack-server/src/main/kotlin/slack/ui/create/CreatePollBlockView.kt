package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.PollOption
import core.model.PollType
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

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
            blockId(UIConstant.BlockID.QUESTION)
            label(QUESTION_SECTION_HEADER_TITLE)
            element {
                plainTextInput {
                    actionId(UIConstant.ActionID.POLL_QUESTION)
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
                PollType.SINGLE_CHOICE -> CreatePollEditableOptionsBlockView(options)
                PollType.AGREE_DISAGREE -> CreatePollStaticOptionsBlockView(pollType, options)
                PollType.ONE_TO_N -> CreatePollStaticOptionsNumberedBlockView(pollType, options)
            }
        }
    }
}
