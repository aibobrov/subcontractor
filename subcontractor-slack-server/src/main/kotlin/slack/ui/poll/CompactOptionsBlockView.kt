package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.dsl.BlockElementDsl
import core.model.PollOption
import core.model.base.Poll
import core.model.base.PollID
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class CompactOptionsBlockView(private val pollID: PollID, private val options: List<PollOption>) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        buildCompactTitle(builder, options)
    }

    private fun buildCompactTitle(builder: LayoutBlockDsl, options: List<PollOption>) {
        fun buildButton(builder: BlockElementDsl, option: PollOption) {
            builder.button {
                text(option.content, emoji = true)
                actionId(UIConstant.ActionID.voteAction(pollID, option.id))
                value(option.id)
            }
        }
        builder.actions {
            elements {
                options.forEach { buildButton(this, it) }
            }
        }
    }
}
