package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import slack.model.SlackPollBuilder
import slack.model.SlackPollBuilderValidator
import slack.model.SlackUIFactory
import slack.server.base.SlackViewBlockActionWebhook
import slack.server.base.SlackDataFactory
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.model.SlackPollMetadata

abstract class SlackViewPollCreationSettingAction<Data: ViewIdentifiable>(
    provider: SlackRequestProvider,
    private val creationRepository: SlackPollCreationRepository,
    dataFactory: SlackDataFactory<BlockActionRequest, ActionContext, Data>
) : SlackViewBlockActionWebhook<Data, SlackPollMetadata>(
    provider,
    dataFactory,
    SlackPollMetadata::class.java
) {
    override fun handle(metadata: SlackPollMetadata, content: Data) {
        val builder = creationRepository.get(metadata.pollID) ?: throw IllegalArgumentException()
        update(builder, content)

        val audienceFuture = provider.audienceList()
        audienceFuture.thenAccept { audience ->
            val errors = SlackPollBuilderValidator.validate(builder)
            val view = SlackUIFactory.creationView(metadata, builder, audience, errors)
            provider.updateView(view, content.viewID)
        }
    }

    abstract fun update(builder: SlackPollBuilder, content: Data)
}
