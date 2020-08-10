package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.base.PollID
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class DelegationBlockView(private val pollID: PollID) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        buildDelegateBlock(builder)
    }

    private fun buildDelegateBlock(builder: LayoutBlockDsl) {
        builder.section {
            plainText(DELEGATE_LABEL_TITLE)
            blockId(pollID)
            usersSelect {
                actionId(UIConstant.ActionID.DELEGATE_VOTE)
                placeholder(DELEGATE_USER_SELECT_PLACEHOLDER)
            }
        }
    }

    companion object {
        internal const val DELEGATE_LABEL_TITLE = "Delegate vote"
        internal const val DELEGATE_USER_SELECT_PLACEHOLDER = "Select user"
    }
}
