package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import com.slack.api.model.view.ViewState
import core.model.PollCreationTime
import core.model.SlackConversation
import core.model.base.UserID
import core.model.storage.LiquidPollRepository
import service.VotingBusinessLogic
import slack.model.*
import slack.server.base.SlackViewSubmissionDataFactory
import slack.server.base.SlackViewSubmissionWebhook
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant

class SlackPollCreationViewSubmission(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository,
    private val liquidPollRepository: LiquidPollRepository,
    private val businessLogic: VotingBusinessLogic
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

        liquidPollRepository.put(metadata.pollID, newPoll.author.id, newPoll)

        val slackUsers = mutableListOf<UserID>()

        for (conversation in builder.audience) {
            val users = provider
                .usersList(conversation.id)
                .thenApply { usersList ->
                    usersList?.map { user : SlackUser -> user.id }
                }
            users.get()?.let { slackUsers.addAll(it) } ?: run { slackUsers.add(conversation.id) }
        }

        businessLogic.addVoters(metadata.pollID, slackUsers)

        val resultInfo = SlackVoteResultsFactory.emptyVoteResults(newPoll)

        val blocks = SlackUIFactory.createPollBlocks(newPoll, resultInfo)

        val pollText = UIConstant.Text.pollText(newPoll)

        val creationTimes = mutableMapOf<SlackConversation, PollCreationTime>()

        // TODO: store audience(conversation identifiers/messages)? separately?
        for (conversation in builder.audience) {
            val pair = provider.sendChatMessage(pollText, blocks, conversation.id, newPoll.votingTime)
            pair.get()?.let { creationTimes[SlackConversation(it.first)] = PollCreationTime(it.second) }
        }

        liquidPollRepository.putPollTime(metadata.pollID, creationTimes)

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
