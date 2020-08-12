package slack.ui.components

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import slack.ui.base.SlackBlockUIRepresentable

class PollTitleBlockView(private val question: String): SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        buildTitle(builder, question)
    }

    private fun buildTitle(builder: LayoutBlockDsl, title: String) {
        builder.section {
            plainText(title)
        }
    }
}
