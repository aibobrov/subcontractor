package slack.server.webhooks

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import core.model.PollAuthor
import core.model.PollType
import core.model.base.ChannelID
import core.model.base.UserID
import slack.model.*
import slack.server.base.SlackSlashCommandDataFactory
import slack.server.base.SlackSlashCommandWebhook
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import java.util.*

class SlackPollCreationSlashCommand(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
) : SlackSlashCommandWebhook<CreationSlashCommandData>(
    provider, CreationSlashCommandData.Companion
) {
    override val commandName: String = "/liquid"

    override fun handle(content: CreationSlashCommandData) {
        val metadata = SlackPollMetadata(UUID.randomUUID().toString())

        val builder = BuilderFactory.createBuilder(
            id = metadata.pollID,
            author = PollAuthor(content.userID, content.userName),
            type = PollType.DEFAULT
        ).apply { audience = SlackAudience(listOf(SlackConversation(content.channelID))) }
        creationRepository.put(metadata.pollID, builder)

        val errors = SlackPollBuilderValidator.validate(builder)
        val view = SlackUIFactory.creationView(metadata, builder, errors)
        provider.openView(view, content.triggerID)
    }
}

data class CreationSlashCommandData(
    val triggerID: String,
    val channelID: ChannelID,
    val userID: UserID,
    val userName: String
) {
    companion object : SlackSlashCommandDataFactory<CreationSlashCommandData> {
        override fun fromRequest(request: SlashCommandRequest, context: SlashCommandContext): CreationSlashCommandData {
            return CreationSlashCommandData(
                request.payload.triggerId,
                request.payload.channelId,
                request.payload.userId,
                request.payload.userName
            )
        }
    }
}
