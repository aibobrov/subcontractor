package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import service.VotingBusinessLogic
import slack.model.SlackManageMetadata
import slack.model.SlackUIFactory
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.SlackViewBlockActionWebhook
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant

class SlackDelegationRuleDeleteAction(provider: SlackRequestProvider, private val businessLogic: VotingBusinessLogic) :
    SlackViewBlockActionWebhook<SlackDelegationRuleDeleteData, SlackManageMetadata>(
        provider,
        SlackDelegationRuleDeleteData.Companion,
        SlackManageMetadata::class.java
    ) {
    override val actionID: String = UIConstant.ActionID.DELETE_DELEGATION_RULE

    override fun handle(metadata: SlackManageMetadata, content: SlackDelegationRuleDeleteData) {
        businessLogic.deleteDelegationRule(metadata.userID, content.ruleID)
        val rules = businessLogic.delegationRules(metadata.userID)

        val view = SlackUIFactory.delegationRulesView(metadata, rules, listOf())
        provider.updateView(view, content.viewID)
    }
}

data class SlackDelegationRuleDeleteData(val viewID: String, val ruleID: String) {
    companion object : SlackBlockActionDataFactory<SlackDelegationRuleDeleteData> {
        override fun fromRequest(request: BlockActionRequest, context: ActionContext): SlackDelegationRuleDeleteData {
            val action = request.payload.actions.first()

            return SlackDelegationRuleDeleteData(
                request.payload.view.id,
                action.value
            )
        }
    }
}