package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.SingleChoicePoll
import core.model.VoteResults
import slack.ui.base.SlackBlockUIRepresentable

class SingleChoicePollBlockView(
    private val poll: SingleChoicePoll,
    optionVoters: VoteResults
) : SlackBlockUIRepresentable {
    val delegationBlockView = DelegationBlockView()
    val optionsBlockView = VerboseOptionsBlockView(poll.id, poll.options, optionVoters)
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
