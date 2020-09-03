package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.view.ViewState
import core.UIRepresentable
import core.model.PollAudience
import core.model.PollCreationTime
import core.model.PollVoter
import core.model.base.UserID
import core.model.base.VotingTime
import core.model.storage.PollInfoStorageImpl
import service.VotingBusinessLogic
import slack.model.*
import slack.server.base.SlackViewSubmissionDataFactory
import slack.server.base.SlackViewSubmissionWebhook
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant
import java.util.concurrent.*

class SlackPollCreationViewSubmission(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository,
    private val pollInfoStorage: PollInfoStorageImpl,
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

        val slackUsers = collectSlackUsers(builder.audience).get()

        businessLogic.register(newPoll, slackUsers)

        val resultInfo = SlackVoteResultsFactory.emptyVoteResults(newPoll)

        val blocks = SlackUIFactory.createPollBlocks(newPoll, resultInfo)

        val pollText = UIConstant.Text.pollText(newPoll)

        val broadcastFuture = slackBroadcastMessage(builder.audience, pollText, blocks, newPoll.votingTime)
        broadcastFuture.thenAccept { creationTimes ->
            pollInfoStorage.putPollCreationTimes(metadata.pollID, creationTimes)
        }
    }

    private fun collectSlackUsers(audience: PollAudience): CompletableFuture<List<UserID>> {
        val userListFutures = audience.map { voter ->
            provider
                .usersList(voter.id)
                .thenApply { usersList ->
                    voter.id to usersList?.map { it.id }
                }
        }

        return CompletableFuture.allOf(*userListFutures.toTypedArray()).thenApply { _ ->
            userListFutures
                .map { it.get() }
                .flatMap { (voterID, users) ->
                    users ?: listOf(voterID)
                }
                .toSet()
                .toList()
        }
    }

    private fun slackBroadcastMessage(
        audience: PollAudience,
        text: String?,
        blocks: UIRepresentable<List<LayoutBlock>>,
        votingTime: VotingTime
    ): CompletableFuture<MutableMap<PollVoter, PollCreationTime>> {
        val sendMessageFutures = audience.map { provider.sendChatMessage(text, blocks, it.id, votingTime) }
        return CompletableFuture.allOf(*sendMessageFutures.toTypedArray()).thenApply { _ ->
            sendMessageFutures
                .mapNotNull { it.get() }
                .map { it.voter to it.time }
                .toMap()
                .toMutableMap()
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
