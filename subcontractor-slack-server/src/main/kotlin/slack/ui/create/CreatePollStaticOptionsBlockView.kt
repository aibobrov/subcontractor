package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.StaticSelectElementBuilder
import com.slack.api.model.kotlin_extension.block.element.dsl.BlockElementDsl
import core.model.OneToNPoll
import core.model.PollOption
import core.model.PollType
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

open class CreatePollStaticOptionsBlockView(
    protected val pollType: PollType,
    options: List<PollOption>
) : SlackBlockUIRepresentable {
    private val optionsBlockView = CreatePollCompactOptionsBlockView(options)

    override fun representIn(builder: LayoutBlockDsl) {
        builder.apply {
            buildPollActionElements(this)
            optionsBlockView.representIn(this)
        }
    }

    private fun buildPollActionElements(builder: LayoutBlockDsl) {
        builder.actions {
            elements {
                buildActionElements(this)
            }
        }
    }

    protected open fun buildActionElements(builder: BlockElementDsl) {
        buildStaticTypeSelection(builder)
    }

    private fun buildStaticTypeSelection(builder: BlockElementDsl) {
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
        builder.staticSelect {
            actionId(UIConstant.ActionID.POLL_TYPE)
            buildStaticPollTypeSelect(this)
            initialOption {
                value(pollType.name)
                plainText(pollType.toString())
            }
        }
    }
}


class CreatePollStaticOptionsNumberedBlockView(
    pollType: PollType,
    options: List<PollOption>
) : CreatePollStaticOptionsBlockView(pollType, options) {
    private val optionsCount = options.size

    override fun buildActionElements(builder: BlockElementDsl) {
        super.buildActionElements(builder)
        buildPollNumberPicker(builder)
    }

    private fun buildPollNumberPicker(builder: BlockElementDsl) {
        builder.staticSelect {
            actionId(UIConstant.ActionID.POLL_NUMBER)
            options {
                OneToNPoll.OPTIONS_RANGE.forEach {
                    option {
                        plainText(it.toString())
                        value(it.toString())
                    }
                }
            }
            initialOption {
                value(optionsCount.toString())
                plainText(optionsCount.toString())
            }
        }
    }
}
