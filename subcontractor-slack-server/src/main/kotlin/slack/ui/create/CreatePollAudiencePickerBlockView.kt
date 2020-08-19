package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.ConversationType
import core.model.PollAudience
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class CreatePollAudiencePickerBlockView(private val audience: PollAudience) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        buildAudienceSelect(builder)
    }

    private fun buildAudienceSelect(builder: LayoutBlockDsl) {
        builder.section {
            markdownText("*$AUDIENCE_HEADER_LABEL*")
            multiConversationsSelect {
                placeholder(AUDIENCE_SELECT_PLACEHOLDER)
                actionId(UIConstant.ActionID.POLL_AUDIENCE)
                defaultToCurrentConversation(audience.isEmpty())
                if (audience.isNotEmpty())
                    initialConversations(*audience.map { it.id }.toTypedArray())
                filter(
                    ConversationType.IM,
                    ConversationType.MULTIPARTY_IM,
                    ConversationType.PRIVATE,
                    ConversationType.PUBLIC,
                    excludeExternalSharedChannels = false,
                    excludeBotUsers = true
                )
            }
        }
    }

    companion object {
        private const val AUDIENCE_HEADER_LABEL = "Audience"
        private const val AUDIENCE_SELECT_PLACEHOLDER = "Select audience"
    }

}
