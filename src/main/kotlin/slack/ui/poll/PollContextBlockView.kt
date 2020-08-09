package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.base.Poll
import slack.ui.base.SlackBlockUIRepresentable

class PollContextBlockView(private val poll: Poll) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        buildContext(builder, poll)
    }

    private fun buildContext(builder: LayoutBlockDsl, poll: Poll) {
        builder.context {
            plainText(
                "Owner @${poll.author.name}  |  🕔  Closes: ${SingleChoicePollBlockView.votingTime(poll.votingTime)}  |  ${SingleChoicePollBlockView.anonymousText(
                    poll.isAnonymous
                )}",
                emoji = true
            )
        }
    }
}
