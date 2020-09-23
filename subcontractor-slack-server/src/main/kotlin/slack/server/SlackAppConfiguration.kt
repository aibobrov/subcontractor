package slack.server

import com.slack.api.bolt.App
import core.model.storage.PollInfoStorage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import service.VotingBusinessLogic
import slack.server.base.RegistrableWebhook
import slack.server.webhooks.*
import slack.service.SlackDelegationRuleRepository
import slack.service.SlackPollCreationRepository
import slack.service.SlackRequestProvider
import slack.ui.base.UIConstant

@Configuration
open class SlackAppConfiguration(
    provider: SlackRequestProvider,
    creationRepository: SlackPollCreationRepository,
    pollInfoStorage: PollInfoStorage,
    businessLogic: VotingBusinessLogic,
    delegationRuleRepository: SlackDelegationRuleRepository
) {
    // Poll creation
    private val liquidCommand = SlackPollCreationSlashCommand(provider, creationRepository)
    private val creationSubmission =
        SlackPollCreationViewSubmission(provider, creationRepository, pollInfoStorage, businessLogic)
    private val editOptionsSubmission = SlackPollEditOptionsViewSubmission(provider, creationRepository)
    private val editOptionAction = SlackViewPollSingleChoiceEditOptionAction(provider, creationRepository)
    private val editOptionAddOptionAction = SlackViewPollEditOptionAddOptionAction(provider, creationRepository)
    private val editOverflowOptionAction =
        SlackViewPollCreationSingleChoicePollOverflowAction(provider, creationRepository)
    private val changeTypeAction = SlackViewPollCreationChangeTypeAction(provider, creationRepository)
    private val numberPickerAction = SlackPollCreationNumberPickerAction(provider, creationRepository)

    // Advanced Settings
    private val anonymousSettingAction = SlackViewPollCreationAnonymousSettingAction(provider, creationRepository)
    private val showResponsesSettingAction =
        SlackViewPollCreationShowResponsesSettingAction(provider, creationRepository)
    private val startDateTimePickerSettingAction =
        SlackViewPollCreationStartDateTimeSettingAction(provider, creationRepository)
    private val finishDateTimePickerSettingAction =
        SlackViewPollCreationFinishDateTimeSettingAction(provider, creationRepository)

    // Date/Time/DateTime picker
    private val startDatePickerAction = SlackViewPollCreationStartDatePickerAction(provider, creationRepository)
    private val startTimePickerAction = SlackViewPollCreationStartTimePickerAction(provider, creationRepository)
    private val finishDatePickerAction = SlackViewPollCreationFinishDatePickerAction(provider, creationRepository)
    private val finishTimePickerAction = SlackViewPollCreationFinishTimePickerAction(provider, creationRepository)

    // Audience
    private val audiencePickerAction = SlackPollCreationAudiencePickerAction(provider, creationRepository)

    // Tags
    private val tagsPickerAction = SlackPollCreationTagPickerAction(provider, creationRepository)

    // Empty action
    private val emptyAction = SlackEmptyAction(UIConstant.ActionID.EMPTY, provider)

    // Voting
    private val delegationAction =
        SlackMessagePollVoteDelegationAction(provider, pollInfoStorage, businessLogic)
    private val voteAction = SlackMessagePollVoteAction(provider, pollInfoStorage, businessLogic)
    private val cancelVoteAction = SlackMessagePollVoteCancelAction(provider, pollInfoStorage, businessLogic)
    private val cancelDelegationAction =
        SlackMessagePollDelegationCancelAction(provider, pollInfoStorage, businessLogic)

    // Rules management
    private val rulesManageCommand = SlackPollRulesManageSlashCommand(provider, businessLogic)
    private val taggedRuleSubmission = SlackTaggedRuleManageViewSubmission(provider)
    private val rulesClearAction = SlackDelegationRulesClearAction(provider, businessLogic)
    private val rulesDeleteAction = SlackDelegationRuleDeleteAction(provider, businessLogic)
    private val rulesAddAction = SlackDelegationRuleAddAction(provider, delegationRuleRepository)
    private val rulesCreateAction = SlackDelegationRuleCreateAction(provider, businessLogic, delegationRuleRepository)
    private val rulesUserSelectAction = SlackDelegationRuleSelectUserAction(provider, delegationRuleRepository)

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
            numberPickerAction,
            anonymousSettingAction,
            showResponsesSettingAction,
            startDateTimePickerSettingAction,
            finishDateTimePickerSettingAction,
            startDatePickerAction,
            startTimePickerAction,
            finishDatePickerAction,
            finishTimePickerAction,
            audiencePickerAction,
            tagsPickerAction,
            emptyAction,
            delegationAction,
            voteAction,
            cancelVoteAction,
            cancelDelegationAction,
            rulesManageCommand,
            taggedRuleSubmission,
            rulesClearAction,
            rulesDeleteAction,
            rulesAddAction,
            rulesCreateAction,
            rulesUserSelectAction
        )

        for (webhook in webhooks) {
            webhook.registerIn(app)
        }
        return app
    }
}
