package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.SingleChoicePoll
import slack.model.SlackVerboseVoteResults
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.components.PollTagsBlockView
import slack.ui.components.PollTitleBlockView

class VerbosePollBlockView(
    private val poll: SingleChoicePoll,
    voteResults: SlackVerboseVoteResults,
    showResults: Boolean
) : SlackBlockUIRepresentable {
    private val titleBlockView = PollTitleBlockView(poll.question)
    private val tagsBlockView = PollTagsBlockView(poll.tags)
    private val delegationBlockView = DelegationBlockView(poll.id)
    private val optionsBlockView = VerboseOptionsBlockView(poll.id, poll.options, voteResults, showResults)
    private val contextBlockView = PollContextBlockView(poll)

    override fun representIn(builder: LayoutBlockDsl) {
        builder.apply {
            titleBlockView.representIn(this)
            tagsBlockView.representIn(this)
            divider()
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
