package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.dsl.BlockElementDsl
import core.model.base.PollID
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class DelegationBlockView(private val pollID: PollID) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        buildDelegateBlock(builder)
    }

    private fun buildDelegateBlock(builder: LayoutBlockDsl) {
        builder.actions {
            blockId(pollID)
            elements {
                buildUserSelect(this)
                buildCancelDelegationButton(this)
                buildCancelVoteButton(this)
            }
        }
    }

    private fun buildCancelDelegationButton(builder: BlockElementDsl) {
        builder.button {
            actionId(UIConstant.ActionID.CANCEL_DELEGATION)
            text(CANCEL_DELEGATION_TITLE)
        }
    }

    private fun buildCancelVoteButton(builder: BlockElementDsl) {
        builder.button {
            actionId(UIConstant.ActionID.CANCEL_VOTE)
            text(CANCEL_VOTE_TITLE)
        }
    }

    private fun buildUserSelect(builder: BlockElementDsl) {
        builder.usersSelect {
            actionId(UIConstant.ActionID.DELEGATE_VOTE)
            placeholder(DELEGATE_USER_SELECT_PLACEHOLDER)
            // TODO: confirm dialog
//            confirm { it }
        }
    }

    companion object {
        internal const val CANCEL_DELEGATION_TITLE = "Cancel delegation"
        internal const val CANCEL_VOTE_TITLE = "Cancel vote"
        internal const val DELEGATE_USER_SELECT_PLACEHOLDER = "Delegate to user"
    }
}
