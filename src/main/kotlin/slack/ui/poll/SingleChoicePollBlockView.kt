package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.SectionBlockBuilder
import com.slack.api.model.kotlin_extension.block.dsl.ContextBlockElementDsl
import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.PollOption
import core.model.SingleChoicePoll
import core.model.VoterInfo
import core.model.base.OptionID
import core.model.base.Text
import slack.ui.base.SlackBlockUIRepresentable


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
            buildDelegateBlock(builder)
        }
    }

    private fun buildPollDescription(builder: LayoutBlockDsl, description: String?) {
        description?.apply {
            builder.context {
                plainText(this@apply)
            }
        }
    }

    private fun buildTitle(builder: LayoutBlockDsl, poll: SingleChoicePoll) {
        builder.section {
            buildText(this, poll.question)
        }
    }

    private fun buildQuestionOption(builder: LayoutBlockDsl, poll: PollOption) {
        builder.section {
            buildText(this, poll.content)
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
    }
}
