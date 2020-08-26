package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.base.ChannelID
import core.model.base.PollID
import core.model.base.UserID
import core.model.errors.VotingError
import core.model.storage.PollCreationTimesStorage
import core.model.storage.PollCreationTimesStorageImpl
import service.VotingBusinessLogic
import slack.model.SlackUIFactory
import slack.model.SlackVoteResultsFactory
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.SlackMessageBlockActionWebhook
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant
import slack.ui.poll.PreviewPollAttachment

class SlackMessagePollVoteDelegationAction(
    provider: SlackRequestProvider,
    private val pollCreationTimesStorage: PollCreationTimesStorage,
    private val businessLogic: VotingBusinessLogic
) : SlackMessageBlockActionWebhook<SlackMessagePollVoteDelegationData>(
    provider,
    SlackMessagePollVoteDelegationData.Companion
) {
    override val actionID: String = UIConstant.ActionID.DELEGATE_VOTE

    override fun handle(content: SlackMessagePollVoteDelegationData) {

        val maybeError = businessLogic.delegate(content.pollID, content.delegatorID, content.userID)

        // Post error about delegation
        if (maybeError == VotingError.CycleFound) {
            provider.postEphemeral(
                UIConstant.Text.delegationError(content.userID),
                content.channelID,
                content.delegatorID
            )
            return
        }

        val poll = businessLogic.get(content.pollID) ?: throw IllegalArgumentException()

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

        val creationTimes = pollCreationTimesStorage.get(poll.id)

        val messageResponse = provider.sendChatMessage(poll.question, blocks, content.userID, poll.votingTime)
        messageResponse.thenAccept {
            if (it != null) {
                creationTimes[it.voter] = it.time
                for (entry in creationTimes) {
                    provider.updateChatMessage(blocks, entry.key.id, entry.value.value)
                }
                pollCreationTimesStorage.put(poll.id, creationTimes)
            }
        }
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
