package slack.server.webhooks

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import core.model.base.ChannelID
import core.model.base.UserID
import service.VotingBusinessLogic
import slack.model.SlackManageMetadata
import slack.model.SlackUIFactory
import slack.server.base.SlackSlashCommandDataFactory
import slack.server.base.SlackSlashCommandWebhook
import slack.service.SlackRequestProvider

class SlackPollRulesManageSlashCommand(provider: SlackRequestProvider, private val businessLogic: VotingBusinessLogic) :
    SlackSlashCommandWebhook<ManageSlashCommandData>(provider, ManageSlashCommandData.Companion) {
    override val commandName: String = "/rules"

    override fun handle(content: ManageSlashCommandData) {
        val rules = businessLogic.delegationRules(content.userID)
        val metadata = SlackManageMetadata(content.userID)
        val ruleManageView = SlackUIFactory.delegationRulesView(metadata, rules, listOf())
        provider.openView(ruleManageView, content.triggerID)
    }
}

data class ManageSlashCommandData(
    val triggerID: String,
    val channelID: ChannelID,
    val userID: UserID,
    val userName: String
) {
    companion object : SlackSlashCommandDataFactory<ManageSlashCommandData> {
        override fun fromRequest(request: SlashCommandRequest, context: SlashCommandContext): ManageSlashCommandData {
            return ManageSlashCommandData(
                request.payload.triggerId,
                request.payload.channelId,
                request.payload.userId,
                request.payload.userName
            )
        }
    }
}