package slack.ui.create

import com.slack.api.model.kotlin_extension.block.composition.dsl.OptionObjectDsl
import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.base.PollTag
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class CreatePollTagsPickerBlockView : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        buildPollTagsPicker(builder, PollTag.values())
    }

    private fun buildPollTagsPicker(builder: LayoutBlockDsl, pollTags: Array<PollTag>) {
        builder.section {
            markdownText("*$POLL_TAGS_HEADER_LABEL*")
            multiStaticSelect {
                placeholder(POLL_TAGS_PLACEHOLDER)
                actionId(UIConstant.ActionID.POLL_TAG)
                options {
                    pollTags.forEach { buildPollTagOption(this, it) }
                }
            }
        }
    }

    private fun buildPollTagOption(builder: OptionObjectDsl, pollTag: PollTag) {
        builder.option {
            value(pollTag.name)
            plainText("#${pollTag.tagName}")
        }
    }

    companion object {
        private const val POLL_TAGS_HEADER_LABEL = "Poll tags"
        private const val POLL_TAGS_PLACEHOLDER = "Select tags"
    }
}