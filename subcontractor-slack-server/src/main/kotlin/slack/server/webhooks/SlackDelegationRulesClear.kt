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

class SlackDelegationRulesClearAction(provider: SlackRequestProvider, private val businessLogic: VotingBusinessLogic) :
    SlackViewBlockActionWebhook<SlackDelegationRulesClearData, SlackManageMetadata>(
        provider,
        SlackDelegationRulesClearData.Companion,
        SlackManageMetadata::class.java
    ) {
    override val actionID: String = UIConstant.ActionID.CLEAR_DELEGATION_RULES

    override fun handle(metadata: SlackManageMetadata, content: SlackDelegationRulesClearData) {
        businessLogic.clearDelegationRules(metadata.userID)
        val rules = businessLogic.delegationRules(metadata.userID)
        val view = SlackUIFactory.delegationRulesView(metadata, rules, listOf())
        provider.updateView(view, content.viewID)
    }
}

data class SlackDelegationRulesClearData(val viewID: String) {
    companion object : SlackBlockActionDataFactory<SlackDelegationRulesClearData> {
        override fun fromRequest(request: BlockActionRequest, context: ActionContext): SlackDelegationRulesClearData {
            return SlackDelegationRulesClearData(request.payload.view.id)
        }
    }
}
