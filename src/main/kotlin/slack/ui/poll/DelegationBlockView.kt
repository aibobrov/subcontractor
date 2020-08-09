package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import slack.ui.base.SlackBlockUIRepresentable

class DelegationBlockView: SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        buildDelegateBlock(builder)
    }

    private fun buildDelegateBlock(builder: LayoutBlockDsl) {
        builder.section {
            plainText(DELEGATE_LABEL_TITLE)
            usersSelect {
                placeholder(DELEGATE_USER_SELECT_PLACEHOLDER)
            }
        }
    }

    companion object {
        internal const val DELEGATE_LABEL_TITLE = "Delegate vote"
        internal const val DELEGATE_USER_SELECT_PLACEHOLDER = "Select user"
    }
}
