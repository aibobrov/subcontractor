package slack.ui.components

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.base.PollTag
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class PollTagsBlockView(private val pollTags: List<PollTag>) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        if (pollTags.isEmpty()) return
        builder.context {
            val blockText = "🏷 ${pollTags.joinToString(" ") { UIConstant.Text.tagText(it) }}"
            markdownText(blockText)
        }
    }
}