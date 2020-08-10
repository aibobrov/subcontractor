package slack.ui.components

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import slack.model.SlackError
import slack.ui.base.SlackBlockUIRepresentable

class ErrorBlockView(private val errors: List<SlackError>) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        for (error in errors) {
            buildError(builder, error)
        }
    }


    private fun buildError(builder: LayoutBlockDsl, error: SlackError) {
        builder.section {
            plainText("$WARNING ${error.localizedMessage}")
        }
    }


    companion object {
        const val WARNING = "⚠️"
    }

}
