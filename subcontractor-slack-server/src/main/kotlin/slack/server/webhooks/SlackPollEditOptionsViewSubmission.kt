package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import core.model.PollOption
import slack.model.SlackPollBuilderValidator
import slack.model.SlackUIFactory
import slack.server.base.SlackViewSubmissionDataFactory
import slack.server.base.SlackViewSubmissionWebhook
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant
import slack.model.SlackPollMetadata

class SlackPollEditOptionsViewSubmission(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
) : SlackViewSubmissionWebhook<EditOptionViewSubmissionData, SlackPollMetadata>(
    provider,
    EditOptionViewSubmissionData.Companion,
    SlackPollMetadata::class.java
) {
    override val callbackID: String = UIConstant.CallbackID.ADD_OPTION_VIEW_SUBMISSION

    override fun handle(metadata: SlackPollMetadata, content: EditOptionViewSubmissionData) {
        val builder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()
        builder.apply { options = content.options }

        val errors = SlackPollBuilderValidator.validate(builder)
        val view = SlackUIFactory.creationView(metadata, builder, errors)
        provider.updateView(view, content.parentViewID)
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
