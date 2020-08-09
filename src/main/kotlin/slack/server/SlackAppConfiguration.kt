package slack.server

import com.slack.api.bolt.App
import core.model.storage.LiquidPollRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import slack.server.base.RegistrableWebhook
import slack.server.webhooks.*
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider

// TODO: research for types of delegation button/picker
// TODO: View components + abstractions
@Configuration
open class SlackAppConfiguration(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository,
    liquidPollRepository: LiquidPollRepository
) {
    // Poll creation
    val liquidCommand = SlackPollCreationSlashCommand(provider, creationRepository)
    val creationSubmission = SlackPollCreationViewSubmission(provider, creationRepository, liquidPollRepository)
    val editOptionsSubmission = SlackPollEditOptionsViewSubmission(provider, creationRepository)
    val editOptionAction = SlackPollSingleChoiceEditOptionAction(provider, creationRepository)
    val editOptionAddOptionAction = SlackPollEditOptionAddOptionAction(provider, creationRepository)
    val editOverflowOptionAction = SlackPollCreationSingleChoicePollOverflowAction(provider, creationRepository)
    val changeTypeAction = SlackPollCreationChangeTypeAction(provider, creationRepository)

    // Advanced Settings
    val anonymousSettingAction = SlackPollCreationAnonymousSettingAction(provider, creationRepository)
    val showResponsesSettingAction = SlackPollCreationShowResponsesSettingAction(provider, creationRepository)
    val startDateTimePickerSettingAction = SlackPollCreationStartDateTimeSettingAction(provider, creationRepository)
    val finishDateTimePickerSettingAction = SlackPollCreationFinishDateTimeSettingAction(provider, creationRepository)

    // Date/Time/DateTime picker
    val startDatePickerAction = SlackPollCreationStartDatePickerAction(provider, creationRepository)
    val startTimePickerAction = SlackPollCreationStartTimePickerAction(provider, creationRepository)
    val finishDatePickerAction = SlackPollCreationFinishDatePickerAction(provider, creationRepository)
    val finishTimePickerAction = SlackPollCreationFinishTimePickerAction(provider, creationRepository)

    @Bean
    open fun initSlackApp(): App {
        val app = App()
        val webhooks: List<RegistrableWebhook> = listOf(
            liquidCommand,
            creationSubmission,
            liquidCommand,
            creationSubmission,
            editOptionsSubmission,
            editOptionAction,
            editOptionAddOptionAction,
            editOverflowOptionAction,
            changeTypeAction,
            anonymousSettingAction,
            showResponsesSettingAction,
            startDateTimePickerSettingAction,
            finishDateTimePickerSettingAction,
            startDatePickerAction,
            startTimePickerAction,
            finishDatePickerAction,
            finishTimePickerAction
        )

        for (webhook in webhooks) {
            webhook.registerIn(app)
        }
        return app
    }
}
