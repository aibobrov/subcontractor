package slack.server.webhooks

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import core.model.PollAuthor
import slack.model.PollBuilder
import core.model.PollType
import slack.server.base.SlackSlashCommandDataFactory
import slack.server.base.SlackSlashCommandWebhook
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.create.CreatePollView
import slack.ui.create.CreationMetadata
import utils.combine
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
        val pollBuilder = PollBuilder(
            id = metadata.pollID,
            author = PollAuthor(content.userID, content.userName),
            type = PollType.SINGLE_CHOICE
        )
        creationRepository.put(metadata.pollID, pollBuilder)

        val audienceFuture = provider.usersList().combine(provider.conversationsList())
        audienceFuture.thenAccept {
            val (users, channels) = it
            val view = CreatePollView(
                metadata,
                pollBuilder.advancedOption,
                pollBuilder.type,
                pollBuilder.options,
                pollBuilder.startTime,
                pollBuilder.finishTime,
                users,
                channels
            )
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
