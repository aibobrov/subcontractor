package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.ContextBlockElementDsl
import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.PollOption
import core.model.base.PollID
import slack.model.SlackUserProfile
import slack.model.SlackVerboseVoteResults
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class VerboseOptionsBlockView(
    private val pollID: PollID,
    private val options: List<PollOption>,
    private val optionVoters: SlackVerboseVoteResults,
    private val showResponses: Boolean
) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        buildPollOptions(builder)
    }

    private fun buildPollOptions(builder: LayoutBlockDsl) {
        options.forEach {
            buildOption(builder, it, optionVoters[it.id] ?: listOf())
        }
    }

    private fun buildQuestionOption(builder: LayoutBlockDsl, option: PollOption) {
        builder.section {
            plainText(option.content)
            accessory {
                button {
                    actionId(UIConstant.ActionID.voteAction(pollID, option.id))
                    text(VerbosePollBlockView.VOTE_BUTTON_TITLE)
                    value(option.id)
                }
            }
        }
    }

    private fun buildVoter(builder: ContextBlockElementDsl, voter: SlackUserProfile) {
        builder.image(imageUrl = voter.profileImageURL, altText = voter.profileName)
    }

    private fun buildQuestionContext(builder: LayoutBlockDsl, voters: List<SlackUserProfile>) {
        builder.context {
            elements {
                if (showResponses) {
                    voters.forEach { buildVoter(this, it) }
                }
                plainText(VerbosePollBlockView.votesCountLabel(voters.size))
            }
        }
    }

    private fun buildOption(builder: LayoutBlockDsl, poll: PollOption, voters: List<SlackUserProfile>) {
        buildQuestionOption(builder, poll)
        buildQuestionContext(builder, voters)
    }
}
