package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.dsl.BlockElementDsl
import core.model.PollOption
import core.model.base.Poll
import slack.ui.base.SlackBlockUIRepresentable

class CompactPollBlockView(private val poll: Poll) : SlackBlockUIRepresentable {
    val delegationBlockView = DelegationBlockView()
    val contextBlockView = PollContextBlockView(poll)
    val compactPollBlockView = CompactOptionsBlockView(poll.options)

    override fun representIn(builder: LayoutBlockDsl) {
        builder.apply {
            buildTitle(this, poll)
            compactPollBlockView.representIn(this)
            divider()
            delegationBlockView.representIn(this)
            divider()
            contextBlockView.representIn(this)
        }
    }

    private fun buildTitle(builder: LayoutBlockDsl, poll: Poll) {
        builder.section {
            plainText(poll.question)
        }
    }


}
