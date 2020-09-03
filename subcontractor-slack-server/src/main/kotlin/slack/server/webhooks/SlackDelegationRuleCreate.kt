package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import core.model.base.PollTag
import service.VotingBusinessLogic
import slack.model.SlackError
import slack.model.SlackManageMetadata
import slack.model.SlackUIFactory
import slack.model.SlackValidator
import slack.server.base.SlackViewSubmissionDataFactory
import slack.server.base.SlackViewSubmissionWebhook
import slack.service.SlackDelegationRuleRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant

class SlackDelegationRuleCreateAction(
    provider: SlackRequestProvider,
    private val businessLogic: VotingBusinessLogic,
    private val delegationRuleRepository: SlackDelegationRuleRepository
) :
    SlackViewSubmissionWebhook<SlackDelegationRuleCreateData, SlackManageMetadata>(
        provider,
        SlackDelegationRuleCreateData.Companion,
        SlackManageMetadata::class.java
    ) {
    override val callbackID: String = UIConstant.CallbackID.CREATE_DELEGATION_RULE_SUBMISSION

    override fun handle(metadata: SlackManageMetadata, content: SlackDelegationRuleCreateData) {
        val builder = delegationRuleRepository.getForUser(metadata.userID) ?: return
        builder.tags = content.tags.toSet()

        val otherRules = businessLogic.delegationRules(metadata.userID)
        val builderErrors = SlackValidator.validate(builder, otherRules)
        if (builderErrors.isNotEmpty()) {
            val view = SlackUIFactory.delegationRuleCreateView(metadata, builderErrors)
            provider.updateView(view, content.viewID)
            throw SlackError.Error
        }
        val delegationRule = builder.build()
        businessLogic.addDelegationRule(delegationRule)

        val rules = businessLogic.delegationRules(metadata.userID)

        val view = SlackUIFactory.delegationRulesView(metadata, rules, listOf())
        provider.updateView(view, content.parentViewID)
    }
}


data class SlackDelegationRuleCreateData(val viewID: String, val parentViewID: String, val tags: List<PollTag>) {
    companion object : SlackViewSubmissionDataFactory<SlackDelegationRuleCreateData> {
        override fun fromRequest(
            request: ViewSubmissionRequest,
            context: ViewSubmissionContext
        ): SlackDelegationRuleCreateData {
            val stateValues = request.payload.view.state.values
            val value =
                stateValues[UIConstant.BlockID.POLL_TAGS_SELECT]?.get(UIConstant.ActionID.POLLTAG_DELEGATION_CREATE)!!

            val pollTags = value.selectedOptions.map { PollTag.valueOf(it.value) }

            return SlackDelegationRuleCreateData(
                request.payload.view.id,
                request.payload.view.previousViewId,
                pollTags
            )
        }
    }
}