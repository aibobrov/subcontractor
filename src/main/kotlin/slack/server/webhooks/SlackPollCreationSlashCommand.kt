package slack.server.webhooks

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import core.model.PollAuthor
import slack.model.SlackPollBuilder
import core.model.PollType
import slack.model.BuilderFactory
import slack.model.SlackPollBuilderValidator
import slack.model.ViewFactory
import slack.server.base.SlackSlashCommandDataFactory
import slack.server.base.SlackSlashCommandWebhook
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.create.CreationMetadata
import java.util.*

class SlackPollCreationSlashCommand(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
) : SlackSlashCommandWebhook<CreationSlashCommandData>(
    provider, CreationSlashCommandData.Companion
) {
    override val commandName: String = "/liquid"

    override fun handle(content: CreationSlashCommandData) {
        val metadata = CreationMetadata(UUID.randomUUID().toString())

        val builder = BuilderFactory.createBuilder(
            id = metadata.pollID,
            author = PollAuthor(content.userID, content.userName),
            type = PollType.SINGLE_CHOICE
        )
        creationRepository.put(metadata.pollID, builder)

        val audienceFuture = provider.audienceList()
        audienceFuture.thenAccept { audience ->
            val errors = SlackPollBuilderValidator.validate(builder)
            val view = ViewFactory.creationView(metadata, builder, audience, errors)
            provider.openView(view, content.triggerID)
        }
    }
}

data class CreationSlashCommandData(
    val triggerID: String,
    val userID: String,
    val userName: String
) {
    companion object : SlackSlashCommandDataFactory<CreationSlashCommandData> {
        override fun fromRequest(request: SlashCommandRequest, context: SlashCommandContext): CreationSlashCommandData {
            return CreationSlashCommandData(
                context.triggerId,
                request.payload.userId,
                request.payload.userName
            )
        }
    }
}
