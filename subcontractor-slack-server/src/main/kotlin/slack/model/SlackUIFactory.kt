package slack.model

import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.view.View
import core.UIRepresentable
import core.model.SingleChoicePoll
import core.model.base.DelegationRule
import core.model.base.Poll
import slack.ui.create.CreatePollView
import slack.ui.create.EditOptionsPollView
import slack.ui.poll.CompactPollBlockView
import slack.ui.poll.VerbosePollBlockView
import slack.ui.rules.CreateDelegationRuleView
import slack.ui.rules.TaggedRuleManageView

object SlackUIFactory {
    fun creationView(
        metadata: SlackPollMetadata,
        builder: SlackPollBuilder,
        errors: List<SlackError>
    ): UIRepresentable<View> {
        return CreatePollView(
            metadata,
            builder.advancedOption,
            builder.type,
            builder.options,
            builder.startTime,
            builder.finishTime,
            builder.audience,
            errors
        )
    }

    fun editOptionsView(metadata: SlackPollMetadata, builder: SlackPollBuilder): UIRepresentable<View> {
        return EditOptionsPollView(
            metadata,
            builder.options
        )
    }

    fun createPollBlocks(poll: Poll, results: SlackPollVoteInfo): UIRepresentable<List<LayoutBlock>> {
        return when {
            poll is SingleChoicePoll && results is SlackPollVoteInfo.Verbose -> {
                createVerbosePollBlocks(poll, results.info)
            }
            else -> createCompactPollBlocks(poll, results.compact())
        }
    }

    fun createCompactPollBlocks(
        poll: Poll,
        results: SlackCompactVoteResults
    ): UIRepresentable<List<LayoutBlock>> {
        val showResponses = poll.showResponses && results.totalVoters > 0
        return CompactPollBlockView(poll, results, showResponses)
    }

    fun createVerbosePollBlocks(
        poll: SingleChoicePoll,
        results: SlackVerboseVoteResults
    ): UIRepresentable<List<LayoutBlock>> {
        val showResponses = poll.showResponses && results.totalVoters > 0
        return VerbosePollBlockView(poll, results, showResponses)
    }


    fun delegationRulesView(metadata: SlackManageMetadata, rules: List<DelegationRule>, errors: List<SlackError>): UIRepresentable<View> {
        return TaggedRuleManageView(metadata, rules, errors)
    }

    fun delegationRuleCreateView(metadata: SlackManageMetadata, errors: List<SlackError>): UIRepresentable<View> {
        return CreateDelegationRuleView(metadata, errors)
    }
}
