package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.base.Poll
import slack.model.SlackCompactVoteResults
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.components.PollResultBlockView
import slack.ui.components.PollTagsBlockView
import slack.ui.components.PollTitleBlockView

class CompactPollBlockView(
    poll: Poll,
    voteResults: SlackCompactVoteResults,
    private val showResults: Boolean
) : SlackBlockUIRepresentable {
    private val titleBlockView = PollTitleBlockView(poll.question)
    private val tagsBlockView = PollTagsBlockView(poll.tags)
    private val delegationBlockView = DelegationBlockView(poll.id)
    private val contextBlockView = PollContextBlockView(poll)
    private val compactPollBlockView = CompactOptionsBlockView(poll.id, poll.options)
    private val voterResultBlockView = PollResultBlockView(poll.options, voteResults, poll.isAnonymous)

    override fun representIn(builder: LayoutBlockDsl) {
        builder.apply {
            titleBlockView.representIn(this)
            tagsBlockView.representIn(this)
            divider()
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

}
