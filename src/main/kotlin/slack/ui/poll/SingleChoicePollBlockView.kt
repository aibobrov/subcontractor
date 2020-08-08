package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.ContextBlockElementDsl
import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.PollOption
import core.model.SingleChoicePoll
import core.model.VoterInfo
import core.model.base.OptionID
import core.model.base.VotingTime
import slack.ui.base.SlackBlockUIRepresentable
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class SingleChoicePollBlockView(
    private val poll: SingleChoicePoll,
    private val optionVoters: Map<OptionID, List<VoterInfo>>
) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        builder.apply {
            buildTitle(this, poll)
            buildPollDescription(this, poll.description)
            divider()
            buildPollOptions(this, poll)
            divider()
            buildDelegateBlock(this)
            divider()
            buildContext(this, poll)
        }
    }

    private fun buildPollDescription(builder: LayoutBlockDsl, description: String?) {
        description?.apply {
            builder.context {
                plainText(this@apply)
            }
        }
    }

    private fun buildContext(builder: LayoutBlockDsl, poll: SingleChoicePoll) {
        builder.context {
            plainText(
                "Owner @${poll.author.name}  |  🕔  Closes: ${votingTime(poll.votingTime)}  |  ${anonymousText(poll.isAnonymous)}",
                emoji = true
            )
        }
    }

    private fun buildTitle(builder: LayoutBlockDsl, poll: SingleChoicePoll) {
        builder.section {
            plainText(poll.question)
        }
    }

    private fun buildQuestionOption(builder: LayoutBlockDsl, option: PollOption) {
        builder.section {
            plainText(option.content)
            accessory {
                button {
                    text(VOTE_BUTTON_TITLE)
                }
            }
        }
    }

    private fun buildVoter(builder: ContextBlockElementDsl, voter: VoterInfo) {
        builder.image(imageUrl = voter.profileImageURL, altText = voter.profileName)
    }

    private fun buildQuestionContext(builder: LayoutBlockDsl, voters: List<VoterInfo>) {
        builder.context {
            elements {
                voters.forEach { buildVoter(this, it) }
                plainText(votesCountLabel(voters.size))
            }
        }
    }

    private fun buildOption(builder: LayoutBlockDsl, poll: PollOption, voters: List<VoterInfo>) {
        buildQuestionOption(builder, poll)
        buildQuestionContext(builder, voters)
    }

    private fun buildPollOptions(builder: LayoutBlockDsl, poll: SingleChoicePoll) {
        poll.options.forEach {
            buildOption(builder, it, optionVoters[it.id] ?: listOf())
        }
    }

    private fun buildDelegateBlock(builder: LayoutBlockDsl) {
        builder.section {
            plainText(DELEGATE_LABEL_TITLE)
            usersSelect {
                placeholder(DELEGATE_USER_SELECT_PLACEHOLDER)
            }
        }
    }

    companion object {
        internal const val VOTE_BUTTON_TITLE = "Vote"
        internal const val DELEGATE_LABEL_TITLE = "Delegate vote"
        internal const val DELEGATE_USER_SELECT_PLACEHOLDER = "Select user"

        internal fun votesCountLabel(count: Int): String {
            return when (count) {
                0 -> "No votes"
                1 -> "1 vote"
                else -> "$count votes"
            }
        }

        fun votingTime(votingTime: VotingTime): String {
            return when (votingTime) {
                VotingTime.Unlimited -> "Never"
                is VotingTime.From -> "Never"
                is VotingTime.Ranged -> votingTime.range.endInclusive.format(DATE_TIME_FORMATTER)
                is VotingTime.UpTo -> votingTime.date.format(DATE_TIME_FORMATTER)
            }
        }

        fun anonymousText(flag: Boolean): String {
            return if (flag) {
                "🔒  Responses are Anonymous"
            } else {
                "🔓  Responses are Non-Anonymous"
            }
        }

        val DATE_TIME_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
    }
}
