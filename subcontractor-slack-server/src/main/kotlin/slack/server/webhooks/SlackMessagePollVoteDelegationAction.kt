package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.AgreeDisagreePoll
import core.model.PollAuthor
import core.model.SingleChoicePoll
import core.model.base.ChannelID
import core.model.base.PollID
import core.model.base.UserID
import core.model.base.VotingTime
import core.model.storage.LiquidPollRepository
import service.VotingBusinessLogic
import slack.model.SlackVerboseVoteResults
import slack.server.base.SlackBlockActionDataFactory
import slack.service.SlackRequestProvider
import slack.server.base.SlackMessageBlockActionWebhook
import slack.ui.base.UIConstant
import slack.ui.poll.VerbosePollBlockView
import java.lang.IllegalArgumentException

class SlackMessagePollVoteDelegationAction(
    provider: SlackRequestProvider,
    private val liquidPollRepository: LiquidPollRepository,
    private val businessLogic: VotingBusinessLogic
) : SlackMessageBlockActionWebhook<SlackMessagePollVoteDelegationData>(
    provider,
    SlackMessagePollVoteDelegationData.Companion
) {
    override val actionID: String = UIConstant.ActionID.DELEGATE_VOTE

    override fun handle(content: SlackMessagePollVoteDelegationData) {
        businessLogic.delegate(content.delegatorID, content.userID)
        val poll = liquidPollRepository.get(content.pollID) ?: throw IllegalArgumentException()
        // TODO: delegation business logic
        val newView = VerbosePollBlockView(
            poll = SingleChoicePoll(
                id = "1",
                question = "+++",
                description = null,
                options = AgreeDisagreePoll.OPTIONS,
                votingTime = VotingTime.Unlimited,
                isFinished = false,
                showResponses = true,
                isAnonymous = false,
                author = PollAuthor(content.userID, "nsartbobrov"),
                tags = listOf()
            ),
            voteResults = SlackVerboseVoteResults(mapOf()),
            showResults = true
        )
        provider.updateChatMessage(newView, content.channelID, content.ts)
    }
}

data class SlackMessagePollVoteDelegationData(
    val delegatorID: UserID,
    val userID: UserID,
    val pollID: PollID,
    val ts: String,
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
