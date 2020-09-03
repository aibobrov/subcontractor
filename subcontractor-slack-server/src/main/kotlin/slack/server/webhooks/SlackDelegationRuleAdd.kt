package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.base.DelegationRule
import slack.model.SlackManageMetadata
import slack.model.SlackUIFactory
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.SlackViewBlockActionWebhook
import slack.service.SlackDelegationRuleRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant
import java.util.*

class SlackDelegationRuleAddAction(
    provider: SlackRequestProvider,
    private val delegationRuleRepository: SlackDelegationRuleRepository
) :
    SlackViewBlockActionWebhook<SlackDelegationRuleAddData, SlackManageMetadata>(
        provider,
        SlackDelegationRuleAddData.Companion,
        SlackManageMetadata::class.java
    ) {
    override val actionID: String = UIConstant.ActionID.ADD_DELEGATION_RULE

    override fun handle(metadata: SlackManageMetadata, content: SlackDelegationRuleAddData) {
        val view = SlackUIFactory.delegationRuleCreateView(metadata, listOf())
        val builder = DelegationRule.Builder(UUID.randomUUID().toString(), metadata.userID)
        delegationRuleRepository.setBuilder(builder)
        provider.pushView(view, content.triggerID)
    }
}

data class SlackDelegationRuleAddData(val triggerID: String) {
    companion object : SlackBlockActionDataFactory<SlackDelegationRuleAddData> {
        override fun fromRequest(request: BlockActionRequest, context: ActionContext): SlackDelegationRuleAddData {
            return SlackDelegationRuleAddData(request.payload.triggerId)
        }
    }
}