package slack.ui.base

import com.slack.api.model.kotlin_extension.block.container.MultiLayoutBlockContainer
import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.view.View
import core.BuildableUIRepresentable

interface SlackBlockUIRepresentable : BuildableUIRepresentable<LayoutBlockDsl, SlackUI> {
    override fun representation(): SlackUI {
        val builder = MultiLayoutBlockContainer()
        return builder.also(this::representIn).underlying
    }
}

interface SlackViewUIRepresentable : BuildableUIRepresentable<View.ViewBuilder, View> {
    override fun representation(): View {
        val builder = View.builder()
        representIn(builder)
        return builder.build()
    }
}

