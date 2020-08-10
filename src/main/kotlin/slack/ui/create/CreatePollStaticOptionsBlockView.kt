package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.StaticSelectElementBuilder
import core.model.PollOption
import core.model.PollType
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class CreatePollStaticOptionsBlockView(options: List<PollOption>) : SlackBlockUIRepresentable {
    private val optionsBlockView = CreatePollCompactOptionsBlockView(options)

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
            }
        }
    }

    companion object {
        private val POLL_TYPE = PollType.AGREE_DISAGREE
    }
}
