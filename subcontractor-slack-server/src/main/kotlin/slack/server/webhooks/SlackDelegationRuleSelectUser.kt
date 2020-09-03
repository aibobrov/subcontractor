package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.base.UserID
import slack.model.SlackManageMetadata
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.SlackViewBlockActionWebhook
import slack.service.SlackDelegationRuleRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant

class SlackDelegationRuleSelectUserAction(
    provider: SlackRequestProvider,
    private val delegationRuleRepository: SlackDelegationRuleRepository
) :
    SlackViewBlockActionWebhook<SlackDelegationRuleSelectUserData, SlackManageMetadata>(
        provider,
        SlackDelegationRuleSelectUserData.Companion,
        SlackManageMetadata::class.java
    ) {
    override val actionID: String = UIConstant.ActionID.USER_DELEGATION_SELECT

    override fun handle(metadata: SlackManageMetadata, content: SlackDelegationRuleSelectUserData) {
        val builder = delegationRuleRepository.getForUser(metadata.userID) ?: return
        builder.toUserID = content.userID
    }
}

data class SlackDelegationRuleSelectUserData(val viewID: String, val userID: UserID) {
    companion object : SlackBlockActionDataFactory<SlackDelegationRuleSelectUserData> {
        override fun fromRequest(
            request: BlockActionRequest,
            context: ActionContext
        ): SlackDelegationRuleSelectUserData {
            val action = request.payload.actions.first()
            return SlackDelegationRuleSelectUserData(
                request.payload.view.id,
                action.selectedUser
            )
        }
    }
}