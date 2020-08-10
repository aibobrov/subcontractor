package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.base.Poll
import core.model.base.VotingTime
import slack.ui.base.SlackBlockUIRepresentable
import utils.unixTimestamp

class PollContextBlockView(private val poll: Poll) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        buildContext(builder, poll)
    }

    private fun buildContext(builder: LayoutBlockDsl, poll: Poll) {
        builder.context {
            markdownText(
                "Owner: <@${poll.author.id}>  |  🕔  Closes: ${votingTime(poll.votingTime)}  |  ${anonymousText(poll.isAnonymous)}"
            )
        }
    }

    companion object {

        fun votingTime(votingTime: VotingTime): String {
            fun format(timestamp: Long) = "<!date^$timestamp^{date_short_pretty} at {time}|Never>"

            return when (votingTime) {
                VotingTime.Unlimited -> "Never"
                is VotingTime.From -> "Never"
                is VotingTime.Ranged -> format(votingTime.range.endInclusive.unixTimestamp)
                is VotingTime.UpTo -> format(votingTime.date.unixTimestamp)
            }
        }

        fun anonymousText(flag: Boolean): String {
            return if (flag) {
                "🔒  Responses are Anonymous"
            } else {
                "🔓  Responses are Non-Anonymous"
            }
        }
    }
}
