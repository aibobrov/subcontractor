package slack.server.webhooks

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import slack.model.SlackPollBuilder
import slack.server.base.SlackBlockActionDataFactory
import slack.server.base.ViewIdentifiable
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant

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

class SlackViewPollCreationAnonymousSettingAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackViewPollCreationSettingAction<SlackPollCreationAdvancedSettingsData>(
    provider,
    creationRepository,
    SlackPollCreationAdvancedSettingsData.Companion
) {
    override val actionID: String = UIConstant.ActionID.ANONYMOUS_TOGGLE

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationAdvancedSettingsData) {
        builder.apply {
            advancedOption = advancedOption.copy(isAnonymous = !advancedOption.isAnonymous)
        }
    }
}

class SlackViewPollCreationShowResponsesSettingAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackViewPollCreationSettingAction<SlackPollCreationAdvancedSettingsData>(
    provider,
    creationRepository,
    SlackPollCreationAdvancedSettingsData.Companion
) {
    override val actionID: String = UIConstant.ActionID.SHOW_RESPONSES_TOGGLE

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationAdvancedSettingsData) {
        builder.apply {
            advancedOption = advancedOption.copy(showResponses = !advancedOption.showResponses)
        }
    }
}

class SlackViewPollCreationFinishDateTimeSettingAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackViewPollCreationSettingAction<SlackPollCreationAdvancedSettingsData>(
    provider,
    creationRepository,
    SlackPollCreationAdvancedSettingsData.Companion
) {
    override val actionID: String = UIConstant.ActionID.FINISH_DATETIME_TOGGLE

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationAdvancedSettingsData) {
        builder.apply {
            advancedOption = advancedOption.copy(finishDateTimeEnabled = !advancedOption.finishDateTimeEnabled)
        }
    }
}

class SlackViewPollCreationStartDateTimeSettingAction(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository
) : SlackViewPollCreationSettingAction<SlackPollCreationAdvancedSettingsData>(
    provider,
    creationRepository,
    SlackPollCreationAdvancedSettingsData.Companion
) {
    override val actionID: String = UIConstant.ActionID.START_DATETIME_TOGGLE

    override fun update(builder: SlackPollBuilder, content: SlackPollCreationAdvancedSettingsData) {
        builder.apply {
            advancedOption = advancedOption.copy(startDateTimeEnabled = !advancedOption.startDateTimeEnabled)
        }
    }
}

