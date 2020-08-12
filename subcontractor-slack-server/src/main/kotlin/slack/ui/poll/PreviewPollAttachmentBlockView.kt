package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.base.Poll
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant
import slack.ui.components.PollTitleBlockView

class PreviewPollAttachmentBlockView(
    poll: Poll,
    private val permalink: String
) : SlackBlockUIRepresentable {
    private val titleBlockView = PollTitleBlockView(poll.question)
    private val contextBlockView = PollContextBlockView(poll)

    override fun representIn(builder: LayoutBlockDsl) {
        builder.apply {
            titleBlockView.representIn(this)
            contextBlockView.representIn(this)
            buildOriginalLinkMessage(this)
        }
    }

    private fun buildOriginalLinkMessage(builder: LayoutBlockDsl) {
        builder.section {
            markdownText(UIConstant.Text.originalMessageText(permalink))
        }
    }
}
