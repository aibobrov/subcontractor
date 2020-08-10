package slack.model

import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.view.View
import core.UIRepresentable
import core.model.SingleChoicePoll
import core.model.VoteResults
import core.model.base.Poll
import slack.ui.create.CreatePollView
import slack.ui.create.EditOptionsPollView
import slack.ui.poll.CompactPollBlockView
import slack.ui.poll.SingleChoicePollBlockView

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

    fun createPollBlocks(poll: Poll, results: VoteResults): UIRepresentable<List<LayoutBlock>> {
        return when (poll) {
            is SingleChoicePoll -> SingleChoicePollBlockView(poll, results)
            else -> CompactPollBlockView(poll)
        }
    }
}
