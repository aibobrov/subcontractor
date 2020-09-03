package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.base.PollID
import core.model.base.UserID
import core.model.storage.PollInfoStorageImpl
import service.VotingBusinessLogic
import slack.model.SlackUIFactory
import slack.model.SlackVoteResultsFactory
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.SlackMessageBlockActionWebhook
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant
import java.lang.IllegalArgumentException

class SlackMessagePollVoteCancelAction(
    provider: SlackRequestProvider,
    private val pollInfoStorage: PollInfoStorageImpl,
    private val businessLogic: VotingBusinessLogic
) : SlackMessageBlockActionWebhook<SlackMessagePollVoteCancelData>(
    provider,
    SlackMessagePollVoteCancelData.Companion
) {
    override val actionID: String = UIConstant.ActionID.CANCEL_VOTE

    override fun handle(content: SlackMessagePollVoteCancelData) {
        val poll = businessLogic.getPoll(content.pollID) ?: throw IllegalArgumentException()

        businessLogic.cancelVote(poll.id, content.userID)

        val voteResults = businessLogic.voteResults(poll.id)
        val compactVoteResults = SlackVoteResultsFactory.compactVoteResults(voteResults)
        val voteInfo = SlackVoteResultsFactory.voteResults(poll, compactVoteResults, provider)

        val blocks = SlackUIFactory.createPollBlocks(poll, voteInfo)

        val times = pollInfoStorage.getPollCreationTimes(poll.id)

        for (entry in times.entries) {
            provider.updateChatMessage(blocks, entry.key.id, entry.value.value)
        }
    }
}

data class SlackMessagePollVoteCancelData(val pollID: PollID, val userID: UserID) {
    companion object : SlackBlockActionDataFactory<SlackMessagePollVoteCancelData> {
        override fun fromRequest(
            request: BlockActionRequest,
            context: ActionContext
        ): SlackMessagePollVoteCancelData {
            val action = request.payload.actions.first()
            return SlackMessagePollVoteCancelData(
                action.blockId,
                request.payload.user.id
            )
        }
    }
}