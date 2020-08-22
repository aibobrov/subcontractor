package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.base.PollID
import core.model.base.UserID
import core.model.storage.PollCreationTimesStorageImpl
import service.VotingBusinessLogic
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.SlackMessageBlockActionWebhook
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant

class SlackMessagePollVoteCancelAction(
    provider: SlackRequestProvider,
    private val pollCreationTimesStorage: PollCreationTimesStorageImpl,
    private val businessLogic: VotingBusinessLogic
) : SlackMessageBlockActionWebhook<SlackMessagePollVoteCancelData>(
    provider,
    SlackMessagePollVoteCancelData.Companion
) {
    override val actionID: String = UIConstant.ActionID.CANCEL_VOTE

    override fun handle(content: SlackMessagePollVoteCancelData) {
        TODO("Not yet implemented")
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