package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import core.model.PollOption
import slack.server.base.SlackViewSubmissionDataFactory
import slack.server.base.SlackViewSubmissionWebhook
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.create.CreatePollView
import slack.ui.create.CreationConstant
import slack.ui.create.CreationMetadata
import utils.combine

class SlackPollEditOptionsViewSubmission(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
) : SlackViewSubmissionWebhook<EditOptionViewSubmissionData, CreationMetadata>(
    provider,
    EditOptionViewSubmissionData.Companion,
    CreationMetadata::class.java
) {
    override val callbackID: String = CreationConstant.CallbackID.ADD_OPTION_VIEW_SUBMISSION

    override fun handle(metadata: CreationMetadata, content: EditOptionViewSubmissionData) {
        val pollBuilder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()
        pollBuilder.apply { options = content.options }
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
            provider.updateView(view, content.parentViewID)
        }
    }
}

data class EditOptionViewSubmissionData(
    val parentViewID: String,
    val options: List<PollOption>
) {
    companion object : SlackViewSubmissionDataFactory<EditOptionViewSubmissionData> {
        override fun fromRequest(
            request: ViewSubmissionRequest,
            context: ViewSubmissionContext
        ): EditOptionViewSubmissionData {
            val viewState = request.payload.view.state
            val options = viewState.values.values.flatMap { id2StateMap ->
                id2StateMap.entries.map {
                    val id = it.key
                    val content = it.value.value
                    PollOption(id, content)
                }
            }

            return EditOptionViewSubmissionData(
                request.payload.view.previousViewId,
                options
            )
        }
    }
}
