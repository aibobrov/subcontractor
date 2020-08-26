package slack.ui.components

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.base.PollTag
import slack.ui.base.SlackBlockUIRepresentable

class PollTagsBlockView(private val pollTags: List<PollTag>) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        if (pollTags.isEmpty()) return
        builder.context {
            val blockText = pollTags.joinToString(" ") { textOf(it) }
            markdownText(blockText)
        }
    }

    companion object {
        fun textOf(pollTag: PollTag): String = "*_#${pollTag.tagName}_*"
    }
}