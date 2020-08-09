package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import slack.model.SlackPollBuilder
import slack.model.SlackPollBuilderValidator
import slack.model.ViewFactory
import slack.server.base.SlackBlockActionCommandWebhook
import slack.server.base.SlackDataFactory
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.create.CreationMetadata

abstract class SlackPollCreationSettingAction<Data: ViewIdentifiable>(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository,
    dataFactory: SlackDataFactory<BlockActionRequest, ActionContext, Data>
) : SlackBlockActionCommandWebhook<Data, CreationMetadata>(
    provider,
    dataFactory,
    CreationMetadata::class.java
) {
    override fun handle(metadata: CreationMetadata, content: Data) {
        val builder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()
        update(builder, content)

        val audienceFuture = provider.audienceList()
        audienceFuture.thenAccept { audience ->
            val errors = SlackPollBuilderValidator.validate(builder)
            val view = ViewFactory.creationView(metadata, builder, audience, errors)
            provider.updateView(view, content.viewID)
        }
    }

    abstract fun update(builder: SlackPollBuilder, content: Data)
}
