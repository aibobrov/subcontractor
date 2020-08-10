package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import com.slack.api.model.view.ViewState
import core.model.SingleChoicePoll
import core.model.VoteResults
import core.model.storage.LiquidPollRepository
import slack.model.*
import slack.server.base.SlackViewSubmissionDataFactory
import slack.server.base.SlackViewSubmissionWebhook
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant
import slack.model.SlackPollMetadata
import slack.ui.poll.CompactPollBlockView
import slack.ui.poll.SingleChoicePollBlockView

class SlackPollCreationViewSubmission(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository,
    private val liquidPollRepository: LiquidPollRepository
) : SlackViewSubmissionWebhook<CreationViewSubmissionData, SlackPollMetadata>(
    provider,
    CreationViewSubmissionData.Companion,
    SlackPollMetadata::class.java
) {
    override val callbackID: String = UIConstant.CallbackID.CREATION_VIEW_SUBMISSION

    override fun handle(metadata: SlackPollMetadata, content: CreationViewSubmissionData) {
        val builder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()
        val errors = SlackPollBuilderValidator.validate(builder)

        if (errors.isNotEmpty()) {
                val view = SlackUIFactory.creationView(metadata, builder, errors)
                provider.updateView(view, content.viewID)

            throw SlackError.Multiple(errors)
        }
        val newPoll = builder.apply { question = content.question }.build()

        creationRepository.remove(metadata.pollID)

        liquidPollRepository.put(metadata.pollID, newPoll)

        // TODO: results fetch (business logic + slack api request)
        val results = VoteResults(mapOf())
        val blocks = SlackUIFactory.createPollBlocks(newPoll, results)

        // TODO: store audience(conversation identifiers)? separately?
        for (conversation in builder.audience) {
            provider.postChatMessage(blocks, conversation.id)
        }
    }
}


data class CreationViewSubmissionData(
    override val viewID: String,
    val question: String
) : ViewIdentifiable {
    companion object : SlackViewSubmissionDataFactory<CreationViewSubmissionData> {
        override fun fromRequest(
            request: ViewSubmissionRequest,
            context: ViewSubmissionContext
        ): CreationViewSubmissionData {
            val viewState = request.payload.view.state
            val question = fetchQuestion(viewState)

            return CreationViewSubmissionData(
                request.payload.view.id,
                question ?: ""
            )
        }

        private fun fetchQuestion(state: ViewState): String? {
            return state.values[UIConstant.BlockID.QUESTION]?.get(UIConstant.ActionID.POLL_QUESTION)?.value
        }
    }
}
