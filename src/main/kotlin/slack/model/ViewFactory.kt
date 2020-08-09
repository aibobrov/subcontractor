package slack.model

import com.slack.api.model.view.View
import core.UIRepresentable
import slack.ui.create.CreatePollView
import slack.ui.create.CreationMetadata
import slack.ui.create.EditOptionsPollView

object ViewFactory {
    fun creationView(
        metadata: CreationMetadata,
        builder: SlackPollBuilder,
        audience: SlackAudience,
        errors: List<SlackError>
    ): UIRepresentable<View> {
        return CreatePollView(
            metadata,
            builder.advancedOption,
            builder.type,
            builder.options,
            builder.startTime,
            builder.finishTime,
            audience.users,
            audience.channel,
            errors
        )
    }

    fun editOptionsView(
        metadata: CreationMetadata,
        builder: SlackPollBuilder
    ): UIRepresentable<View> {
        return EditOptionsPollView(
            metadata,
            builder.options
        )
    }
}
