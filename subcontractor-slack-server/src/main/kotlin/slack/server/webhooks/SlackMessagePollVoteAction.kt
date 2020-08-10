package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.*
import core.model.base.ChannelID
import core.model.base.OptionID
import core.model.base.PollID
import core.model.storage.LiquidPollRepository
import slack.model.SlackPollVoteInfo
import slack.model.SlackUIFactory
import slack.model.SlackUser
import slack.model.SlackVoteResults
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.SlackPatternBlockActionWebhook
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant
import java.lang.IllegalArgumentException
import java.util.regex.Pattern
import kotlin.math.min
import kotlin.random.Random

class SlackMessagePollVoteAction(
    provider: SlackRequestProvider,
    val liquidPollRepository: LiquidPollRepository
) : SlackPatternBlockActionWebhook<SlackMessagePollVoteData>(
    provider,
    SlackMessagePollVoteData.Companion
) {
    override val actionID: Pattern = UIConstant.ActionID.VOTE

    override fun handle(content: SlackMessagePollVoteData) {
        val poll = liquidPollRepository.get(content.pollID) ?: throw IllegalArgumentException()
        // TODO: voting business logic
        val userList = provider.usersList().get()
        val result = dummy(poll.options, userList.toSet())

        val resultInfo: SlackPollVoteInfo = when (poll.type) {
            PollType.SINGLE_CHOICE -> {
                val ids = userList.map { it.id }
                val profiles = provider.userProfiles(ids).get()

                // TODO: cleanup
                val slackResults = SlackVoteResults(
                    result.mapValues { entry ->
                        entry.value.map { profiles[it.id] ?: error("unreachable") }
                    }
                )
                SlackPollVoteInfo.Verbose(slackResults)
            }
            PollType.AGREE_DISAGREE -> SlackPollVoteInfo.Compact(result)
        }

        val blocks = SlackUIFactory.createPollBlocks(poll, resultInfo)
        provider.updateChatMessage(blocks, content.channelID, content.ts)
    }

    companion object {
        // TODO: remove after business logic
        fun dummy(options: List<PollOption>, users: Set<SlackUser>): VoteResults {
            var usersSet = users
            val map = mutableMapOf<OptionID, List<Voter>>()
            for (option in options) {
                val count = min(Random.nextInt(usersSet.size + 1), usersSet.size)
                val usersList = usersSet.toMutableList().apply { shuffle() }

                map[option.id] = usersList.take(count).map { Voter(it.id, VoteWork.Vote(option.id)) }
                usersSet = usersList.drop(count).toSet()
            }
            return VoteResults(map)
        }
    }
}

data class SlackMessagePollVoteData(
    val pollID: PollID,
    val optionID: OptionID,
    val ts: String,
    val channelID: ChannelID
) {
    companion object : SlackBlockActionDataFactory<SlackMessagePollVoteData> {
        override fun fromRequest(request: BlockActionRequest, context: ActionContext): SlackMessagePollVoteData {
            val action = request.payload.actions.first()
            val pattern = UIConstant.ActionID.VOTE.toRegex()
            val matcher = pattern.findAll(action.actionId).first()

            val (_, pollID, optionID) = matcher.groups.toList()
            return SlackMessagePollVoteData(
                pollID!!.value,
                optionID!!.value,
                request.payload.message.ts,
                request.payload.channel.id
            )
        }
    }
}
