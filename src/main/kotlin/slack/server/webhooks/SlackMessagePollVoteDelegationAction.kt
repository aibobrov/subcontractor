package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.AgreeDisagreePoll
import core.model.PollAuthor
import core.model.SingleChoicePoll
import core.model.VoteResults
import core.model.base.ChannelID
import core.model.base.UserID
import core.model.base.VotingTime
import slack.server.base.SlackBlockActionDataFactory
import slack.service.SlackRequestProvider
import slack.server.base.SlackMessageBlockActionWebhook
import slack.ui.base.UIConstant
import slack.ui.poll.SingleChoicePollBlockView

class SlackMessagePollVoteDelegationAction(
    provider: SlackRequestProvider
) : SlackMessageBlockActionWebhook<SlackMessagePollVoteDelegationData>(
    provider,
    SlackMessagePollVoteDelegationData.Companion
) {
    override val actionID: String = UIConstant.ActionID.DELEGATE_VOTE

    override fun handle(content: SlackMessagePollVoteDelegationData) {
        // TODO: delegation business logic
        val newView = SingleChoicePollBlockView(
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
            optionVoters = VoteResults(mapOf())
        )
        provider.updateChatMessage(newView, content.channelID, content.ts)
    }
}

data class SlackMessagePollVoteDelegationData(
    val delegatorID: UserID,
    val userID: UserID,
    val ts: String,
    val channelID: ChannelID
) {
    companion object : SlackBlockActionDataFactory<SlackMessagePollVoteDelegationData> {
        override fun fromRequest(request: BlockActionRequest, context: ActionContext): SlackMessagePollVoteDelegationData {
            val delegatorID = request.payload.user.id
            val userID = request.payload.actions.first().selectedUser
            return SlackMessagePollVoteDelegationData(
                delegatorID,
                userID,
                request.payload.message.ts,
                request.payload.channel.id
            )
        }
    }
}
