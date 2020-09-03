package slack.server.webhooks

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import core.model.base.ChannelID
import core.model.base.UserID
import service.VotingBusinessLogic
import slack.server.base.SlackSlashCommandDataFactory
import slack.server.base.SlackSlashCommandWebhook
import slack.service.SlackRequestProvider

class SlackPollManageSlashCommand(provider: SlackRequestProvider, private val businessLogic: VotingBusinessLogic) :
    SlackSlashCommandWebhook<ManageSlashCommandData>(provider, ManageSlashCommandData.Companion) {
    override val commandName: String = "/manage"

    override fun handle(content: ManageSlashCommandData) {
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