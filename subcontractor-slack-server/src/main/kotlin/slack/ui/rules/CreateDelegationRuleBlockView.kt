package slack.ui.rules

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.base.PollTag
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class CreateDelegationRuleBlockView : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        builder.input {
            label(" ")
            blockId(UIConstant.BlockID.POLL_TAGS_SELECT)
            multiStaticSelect {
                placeholder(PLACEHOLDER_TEXT)
                actionId(UIConstant.ActionID.POLLTAG_DELEGATION_CREATE)
                options {
                    PollTag.values().forEach {
                        option {
                            value(it.name)
                            plainText(it.tagName)
                        }
                    }
                }
            }
        }

        builder.section {
            plainText(USER_SELECT_TEXT)
            usersSelect {
                actionId(UIConstant.ActionID.USER_DELEGATION_SELECT)
                placeholder(USER_PLACEHOLDER_TEXT)
            }
        }
    }

    companion object {
        private const val PLACEHOLDER_TEXT = "Select tag"
        private const val USER_PLACEHOLDER_TEXT = "Select user"
        private const val USER_SELECT_TEXT = "Always delegate vote to user"

    }
}