package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import slack.model.SlackPollBuilder
import slack.model.SlackPollBuilderValidator
import slack.model.ViewFactory
import slack.server.base.SlackBlockActionCommandWebhook
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.create.CreationConstant
import slack.ui.create.CreationMetadata

data class SlackPollCreationAdvancedSettingsData(override val viewID: String) : ViewIdentifiable {
    companion object : SlackBlockActionDataFactory<SlackPollCreationAdvancedSettingsData> {
        override fun fromRequest(
            request: BlockActionRequest,
            context: ActionContext
        ): SlackPollCreationAdvancedSettingsData {
            return SlackPollCreationAdvancedSettingsData(request.payload.view.id)
        }
    }
}

class SlackPollCreationAnonymousSettingAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackPollCreationSettingAction<SlackPollCreationAdvancedSettingsData>(
    provider,
    creationRepository,
    SlackPollCreationAdvancedSettingsData.Companion
) {
    override val actionID: String = CreationConstant.ActionID.ANONYMOUS_TOGGLE

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationAdvancedSettingsData) {
        builder.apply {
            advancedOption = advancedOption.copy(isAnonymous = !advancedOption.isAnonymous)
        }
    }
}

class SlackPollCreationShowResponsesSettingAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackPollCreationSettingAction<SlackPollCreationAdvancedSettingsData>(
    provider,
    creationRepository,
    SlackPollCreationAdvancedSettingsData.Companion
) {
    override val actionID: String = CreationConstant.ActionID.SHOW_RESPONSES_TOGGLE

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationAdvancedSettingsData) {
        builder.apply {
            advancedOption = advancedOption.copy(showResponses = !advancedOption.showResponses)
        }
    }
}

class SlackPollCreationFinishDateTimeSettingAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackPollCreationSettingAction<SlackPollCreationAdvancedSettingsData>(
    provider,
    creationRepository,
    SlackPollCreationAdvancedSettingsData.Companion
) {
    override val actionID: String = CreationConstant.ActionID.FINISH_DATETIME_TOGGLE

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationAdvancedSettingsData) {
        builder.apply {
            advancedOption = advancedOption.copy(finishDateTimeEnabled = !advancedOption.finishDateTimeEnabled)
        }
    }
}

class SlackPollCreationStartDateTimeSettingAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackPollCreationSettingAction<SlackPollCreationAdvancedSettingsData>(
    provider,
    creationRepository,
    SlackPollCreationAdvancedSettingsData.Companion
) {
    override val actionID: String = CreationConstant.ActionID.START_DATETIME_TOGGLE

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationAdvancedSettingsData) {
        builder.apply {
            advancedOption = advancedOption.copy(startDateTimeEnabled = !advancedOption.startDateTimeEnabled)
        }
    }
}

