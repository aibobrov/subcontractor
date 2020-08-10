package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.SingleChoicePoll
import slack.model.SlackVerboseVoteResults
import slack.ui.base.SlackBlockUIRepresentable

class VerbosePollBlockView(
    private val poll: SingleChoicePoll,
    voteResults: SlackVerboseVoteResults,
    showResults: Boolean
) : SlackBlockUIRepresentable {
    val delegationBlockView = DelegationBlockView(poll.id)
    val optionsBlockView = VerboseOptionsBlockView(poll.id, poll.options, voteResults, showResults)
    val contextBlockView = PollContextBlockView(poll)

    override fun representIn(builder: LayoutBlockDsl) {
        builder.apply {
            buildTitle(this, poll.question)
            buildPollDescription(this, poll.description)
            divider()
            optionsBlockView.representIn(this)
            divider()
            delegationBlockView.representIn(this)
            divider()
            contextBlockView.representIn(this)
        }
    }

    private fun buildPollDescription(builder: LayoutBlockDsl, description: String?) {
        description?.apply {
            builder.context {
                plainText(this@apply)
            }
        }
    }

    private fun buildTitle(builder: LayoutBlockDsl, title: String) {
        builder.section {
            plainText(title)
        }
    }

    companion object {
        internal const val VOTE_BUTTON_TITLE = "Vote"

        internal fun votesCountLabel(count: Int): String {
            return when (count) {
                0 -> "No votes"
                1 -> "1 vote"
                else -> "$count votes"
            }
        }
    }
}
