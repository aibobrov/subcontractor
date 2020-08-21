package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.AgreeDisagreePoll
import core.model.PollAuthor
import core.model.SingleChoicePoll
import core.model.base.ChannelID
import core.model.storage.PollCreationTimesStorageImpl
import core.model.base.PollID
import core.model.base.UserID
import core.model.base.VotingTime
import service.VotingBusinessLogic
import slack.model.SlackUIFactory
import slack.model.SlackVerboseVoteResults
import slack.model.SlackVoteResultsFactory
import slack.server.base.SlackBlockActionDataFactory
import slack.service.SlackRequestProvider
import slack.server.base.SlackMessageBlockActionWebhook
import slack.ui.base.UIConstant
import slack.ui.poll.PreviewPollAttachment
import slack.ui.poll.VerbosePollBlockView
import java.lang.IllegalArgumentException

class SlackMessagePollVoteDelegationAction(
    provider: SlackRequestProvider,
    private val pollCreationTimesStorage: PollCreationTimesStorageImpl,
    private val businessLogic: VotingBusinessLogic
) : SlackMessageBlockActionWebhook<SlackMessagePollVoteDelegationData>(
    provider,
    SlackMessagePollVoteDelegationData.Companion
) {
    override val actionID: String = UIConstant.ActionID.DELEGATE_VOTE

    override fun handle(content: SlackMessagePollVoteDelegationData) {

        businessLogic.delegate(content.pollID, content.delegatorID, content.userID)

        val poll = businessLogic.getPoll(content.pollID) ?: throw IllegalArgumentException()

        // Post info about delegation
        val permalink = provider.getPermanentMessageURL(content.channelID, content.ts)
        permalink.thenCompose {
            val attachment = PreviewPollAttachment(poll, it)
            provider.postEphemeral(
                UIConstant.Text.delegationInfo(content.userID),
                attachment,
                content.channelID,
                content.delegatorID
            )
        }

        val voteResults = businessLogic.voteResults(poll.id)
        val compactVoteResults = SlackVoteResultsFactory.compactVoteResults(voteResults)
        val voteInfo = SlackVoteResultsFactory.voteResults(poll, compactVoteResults, provider)

        val blocks = SlackUIFactory.createPollBlocks(poll, voteInfo)

        provider.sendChatMessage(poll.question, blocks, content.userID, poll.votingTime)
    }
}

data class SlackMessagePollVoteDelegationData(
    val delegatorID: UserID,
    val userID: UserID,
    val pollID: PollID,
    val ts: String, // message timestamp
    val channelID: ChannelID
) {
    companion object : SlackBlockActionDataFactory<SlackMessagePollVoteDelegationData> {
        override fun fromRequest(
            request: BlockActionRequest,
            context: ActionContext
        ): SlackMessagePollVoteDelegationData {
            val delegatorID = request.payload.user.id
            val action = request.payload.actions.first()
            val userID = action.selectedUser
            val pollID = action.blockId
            return SlackMessagePollVoteDelegationData(
                delegatorID,
                userID,
                pollID,
                request.payload.message.ts,
                request.payload.channel.id
            )
        }
    }
}
