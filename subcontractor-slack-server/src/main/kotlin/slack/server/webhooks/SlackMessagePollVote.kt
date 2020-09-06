package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.base.ChannelID
import core.model.base.OptionID
import core.model.base.PollID
import core.model.base.UserID
import core.model.storage.PollInfoStorage
import service.VotingBusinessLogic
import slack.model.SlackUIFactory
import slack.model.SlackVoteResultsFactory
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.SlackPatternBlockActionWebhook
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant
import java.util.regex.Pattern

class SlackMessagePollVoteAction(
    provider: SlackRequestProvider,
    private val pollInfoStorage: PollInfoStorage,
    private val businessLogic: VotingBusinessLogic
) : SlackPatternBlockActionWebhook<SlackMessagePollVoteData>(
    provider,
    SlackMessagePollVoteData.Companion
) {
    override val actionID: Pattern = UIConstant.ActionID.VOTE

    override fun handle(content: SlackMessagePollVoteData) {
        businessLogic.vote(content.pollID, content.userID, content.optionID)
        val poll = pollInfoStorage.getPoll(content.pollID) ?: throw IllegalArgumentException()

        val voteResults = businessLogic.voteResults(poll.id)
        val compactVoteResults = SlackVoteResultsFactory.compactVoteResults(voteResults)
        val voteInfo = SlackVoteResultsFactory.voteResults(poll, compactVoteResults, provider)

        val blocks = SlackUIFactory.createPollBlocks(poll, voteInfo)

        val times = pollInfoStorage.getPollCreationTimes(poll.id)

        for ((voter, time) in times.entries) {
            provider.updateChatMessage(blocks, voter.id, time.value)
        }
    }
}

data class SlackMessagePollVoteData(
    val userID: UserID,
    val pollID: PollID,
    val optionID: OptionID,
    val ts: String,
    val channelID: ChannelID
) {
    companion object : SlackBlockActionDataFactory<SlackMessagePollVoteData> {
        override fun fromRequest(request: BlockActionRequest, context: ActionContext): SlackMessagePollVoteData {
            val userID = request.payload.user.id
            val action = request.payload.actions.first()
            val pattern = UIConstant.ActionID.VOTE.toRegex()
            val matcher = pattern.findAll(action.actionId).first()

            val (_, pollID, optionID) = matcher.groups.toList()
            return SlackMessagePollVoteData(
                userID,
                pollID!!.value,
                optionID!!.value,
                request.payload.message.ts,
                request.payload.channel.id
            )
        }
    }
}
