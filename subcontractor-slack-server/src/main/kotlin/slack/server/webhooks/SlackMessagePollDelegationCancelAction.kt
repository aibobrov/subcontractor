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

class SlackMessagePollDelegationCancelAction(
    provider: SlackRequestProvider,
    private val pollCreationTimesStorage: PollCreationTimesStorageImpl,
    private val businessLogic: VotingBusinessLogic
) : SlackMessageBlockActionWebhook<SlackMessagePollDelegationCancelData>(
    provider,
    SlackMessagePollDelegationCancelData.Companion
) {
    override val actionID: String = UIConstant.ActionID.CANCEL_DELEGATION

    override fun handle(content: SlackMessagePollDelegationCancelData) {
        TODO("Not yet implemented")
    }
}

data class SlackMessagePollDelegationCancelData(val pollID: PollID, val userID: UserID) {
    companion object : SlackBlockActionDataFactory<SlackMessagePollDelegationCancelData> {
        override fun fromRequest(
            request: BlockActionRequest,
            context: ActionContext
        ): SlackMessagePollDelegationCancelData {
            val action = request.payload.actions.first()
            return SlackMessagePollDelegationCancelData(
                action.blockId,
                request.payload.user.id
            )
        }
    }
}