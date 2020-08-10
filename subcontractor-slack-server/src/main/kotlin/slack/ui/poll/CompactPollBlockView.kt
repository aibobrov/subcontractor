package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.VoteResults
import core.model.base.Poll
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.components.PollResultBlockView

class CompactPollBlockView(
    private val poll: Poll,
    voteResults: VoteResults,
    private val showResults: Boolean
) : SlackBlockUIRepresentable {
    val delegationBlockView = DelegationBlockView()
    val contextBlockView = PollContextBlockView(poll)
    val compactPollBlockView = CompactOptionsBlockView(poll.id, poll.options)
    val voterResultBlockView = PollResultBlockView(poll.options, voteResults, poll.isAnonymous)

    override fun representIn(builder: LayoutBlockDsl) {
        builder.apply {
            buildTitle(this, poll)
            compactPollBlockView.representIn(this)
            divider()
            delegationBlockView.representIn(this)
            contextBlockView.representIn(this)
            if (showResults) {
                divider()
                voterResultBlockView.representIn(this)
            }
        }
    }

    private fun buildTitle(builder: LayoutBlockDsl, poll: Poll) {
        builder.section {
            plainText(poll.question)
        }
    }


}
