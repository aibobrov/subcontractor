package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.dsl.BlockElementDsl
import core.model.PollOption
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class CreatePollCompactOptionsBlockView(private val options: List<PollOption>) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        builder.actions {
            elements {
                options.forEach { buildOptions(this, it) }
            }
        }
    }

    private fun buildOptions(builder: BlockElementDsl, option: PollOption) {
        builder.button {
            actionId(UIConstant.ActionID.emptyAction(option.id))
            text(option.content)
        }
    }
}
