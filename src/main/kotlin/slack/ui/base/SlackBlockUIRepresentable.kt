package slack.ui.base

import com.slack.api.model.kotlin_extension.block.SectionBlockBuilder
import com.slack.api.model.kotlin_extension.block.container.MultiLayoutBlockContainer
import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.view.View
import core.BuildableUIRepresentable
import core.model.base.Text

interface SlackBlockUIRepresentable : BuildableUIRepresentable<LayoutBlockDsl, SlackUI> {
    override fun representation(): SlackUI {
        val builder = MultiLayoutBlockContainer()
        return builder.also(this::representIn).underlying
    }

    fun buildText(builder: SectionBlockBuilder, text: Text) {
        when (text) {
            is Text.Plain -> builder.plainText(text.content)
            is Text.Markdown -> builder.markdownText(text.content)
        }
    }
}

interface SlackViewUIRepresentable : BuildableUIRepresentable<View.ViewBuilder, View> {
    override fun representation(): View {
        val builder = View.builder()
        representIn(builder)
        return builder.build()
    }
}

