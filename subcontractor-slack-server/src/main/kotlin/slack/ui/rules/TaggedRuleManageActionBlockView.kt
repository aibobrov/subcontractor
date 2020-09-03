package slack.ui.rules

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.ButtonStyle
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class TaggedRuleManageActionBlockView: SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        builder.actions {
            elements {
                button {
                    text(ADD_NEW_BUTTON_TEXT)
                    actionId(UIConstant.ActionID.ADD_DELEGATION_RULE)
                }
                button {
                    text(CLEAR_ALL_BUTTON_TEXT)
                    style(ButtonStyle.DANGER)
                    actionId(UIConstant.ActionID.CLEAR_DELEGATION_RULES)
                }
            }
        }
    }

    companion object {
        private const val ADD_NEW_BUTTON_TEXT = "Add new"
        private const val CLEAR_ALL_BUTTON_TEXT = "Clear"
    }
}