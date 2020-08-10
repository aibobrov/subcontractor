package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.base.ChannelID
import core.model.base.OptionID
import core.model.base.PollID
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.SlackPatternBlockActionWebhook
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant
import java.util.regex.Pattern

class SlackMessagePollVoteAction(
    provider: SlackRequestProvider
) : SlackPatternBlockActionWebhook<SlackMessagePollVoteData>(
    provider,
    SlackMessagePollVoteData.Companion
) {
    override val actionID: Pattern = UIConstant.ActionID.VOTE

    override fun handle(content: SlackMessagePollVoteData) {
        // TODO: voting business logic
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
            val optionID = action.value
            val pollID = action.blockId
            return SlackMessagePollVoteData(
                pollID,
                optionID,
                request.payload.message.ts,
                request.payload.channel.id
            )
        }
    }
}
