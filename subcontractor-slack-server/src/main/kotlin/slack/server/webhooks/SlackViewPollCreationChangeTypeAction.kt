package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import core.model.PollType
import slack.model.*
import slack.server.base.SlackViewBlockActionWebhook
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant
import slack.model.SlackPollMetadata

class SlackViewPollCreationChangeTypeAction(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository
) : SlackViewBlockActionWebhook<SlackPollCreationChangeTypeData, SlackPollMetadata>(
    provider,
    SlackPollCreationChangeTypeData.Companion,
    SlackPollMetadata::class.java
) {
    override val actionID: String = UIConstant.ActionID.POLL_TYPE

    override fun handle(metadata: SlackPollMetadata, content: SlackPollCreationChangeTypeData) {
        val builder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()

        val newBuilder = BuilderFactory.createBuilder(builder, content.pollType)

        creationRepository.put(metadata.pollID, newBuilder)

        val audienceFuture = provider.audienceList()
        audienceFuture.thenAccept { audience ->
            val errors = SlackPollBuilderValidator.validate(newBuilder)
            val view = SlackUIFactory.creationView(metadata, newBuilder, audience, errors)
            provider.updateView(view, content.viewID)
        }
    }
}

data class SlackPollCreationChangeTypeData(
    override val viewID: String,
    val pollType: PollType
) : ViewIdentifiable {
    companion object : SlackBlockActionDataFactory<SlackPollCreationChangeTypeData> {
        override fun fromRequest(request: BlockActionRequest, context: ActionContext): SlackPollCreationChangeTypeData {
            val action = request.payload.actions.first()
            val pollType = PollType.valueOf(action.selectedOption.value)
            return SlackPollCreationChangeTypeData(request.payload.view.id, pollType)
        }
    }

}
