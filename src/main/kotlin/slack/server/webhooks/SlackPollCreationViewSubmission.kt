package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import com.slack.api.model.view.ViewState
import core.model.PollType
import core.model.SingleChoicePoll
import core.model.storage.LiquidPollRepository
import slack.model.*
import slack.server.base.SlackViewSubmissionDataFactory
import slack.server.base.SlackViewSubmissionWebhook
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.create.CreationConstant
import slack.ui.create.CreationMetadata
import slack.ui.poll.SingleChoicePollBlockView

class SlackPollCreationViewSubmission(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository,
    private val liquidPollRepository: LiquidPollRepository
) : SlackViewSubmissionWebhook<CreationViewSubmissionData, CreationMetadata>(
    provider,
    CreationViewSubmissionData.Companion,
    CreationMetadata::class.java
) {
    override val callbackID: String = CreationConstant.CallbackID.CREATION_VIEW_SUBMISSION

    override fun handle(metadata: CreationMetadata, content: CreationViewSubmissionData) {
        val builder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()
        val errors = SlackPollBuilderValidator.validate(builder)

        // TODO: fix bugs ackWithError
        if (errors.isNotEmpty()) {
            val audienceFuture = provider.audienceList()
            audienceFuture.thenAccept { audience ->
                val view = ViewFactory.creationView(metadata, builder, audience, errors)
                provider.updateView(view, content.viewID)
            }

            throw SlackError.Multiple(errors)
        }
        val newPoll = builder.apply { question = content.question }.build()

        creationRepository.remove(metadata.pollID)

        liquidPollRepository.put(metadata.pollID, newPoll)

        check(newPoll.type == PollType.SINGLE_CHOICE) // TODO: remove after poll type feature

        val pollView = SingleChoicePollBlockView(newPoll as SingleChoicePoll, mapOf())
        for (channel in content.selectedChannels) {
            provider.postChatMessage(pollView, channel.id)
        }

        for (user in content.selectedUsers) {
            provider.postDirectMessage(pollView, user.id)
        }
    }
}


data class CreationViewSubmissionData(
    override val viewID: String,
    val question: String,
    val selectedChannels: List<SlackChannel>,
    val selectedUsers: List<SlackUser>
): ViewIdentifiable {
    companion object : SlackViewSubmissionDataFactory<CreationViewSubmissionData> {
        override fun fromRequest(
            request: ViewSubmissionRequest,
            context: ViewSubmissionContext
        ): CreationViewSubmissionData {
            val viewState = request.payload.view.state
            val question = fetchQuestion(viewState)
            val selectedAudience = fetchSelectedAudience(viewState)

            val channels = mutableListOf<SlackChannel>()
            val users = mutableListOf<SlackUser>()
            for (audience in selectedAudience) {
                val matcher = CHANNEL_NAME_PATTERN.matcher(audience.text.text)

                if (matcher.matches()) {
                    channels.add(SlackChannel(audience.value, matcher.group(1)))
                } else {
                    users.add(SlackUser(audience.value, audience.text.text))
                }
            }

            return CreationViewSubmissionData(
                request.payload.view.id,
                question ?: "",
                channels,
                users
            )
        }

        val CHANNEL_NAME_PATTERN = "# (\\w+)".toPattern()

        private fun fetchQuestion(state: ViewState): String? {
            return state.values[CreationConstant.BlockID.QUESTION]?.get(CreationConstant.ActionID.POLL_QUESTION)?.value
        }

        private fun fetchSelectedAudience(state: ViewState): List<ViewState.SelectedOption> {
            return state.values[CreationConstant.BlockID.AUDIENCE]?.get(CreationConstant.ActionID.POLL_AUDIENCE)?.selectedOptions
                ?: listOf()
        }
    }
}
