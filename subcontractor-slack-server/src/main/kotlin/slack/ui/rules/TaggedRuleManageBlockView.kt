package slack.ui.rules

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.element.ButtonStyle
import core.model.base.DelegationRule
import slack.model.SlackUser
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class TaggedRuleManageBlockView(private val rule: DelegationRule) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        builder.section {
            val tagsText = "🏷 ${rule.tags.joinToString(" ") { UIConstant.Text.tagText(it) }}"
            val sectionText = "$tagsText delegates to ${UIConstant.Text.userText(SlackUser(rule.toUserID))}"
            markdownText(sectionText)
            accessory {
                button {
                    value(rule.id)
                    actionId(UIConstant.ActionID.DELETE_DELEGATION_RULE)
                    style(ButtonStyle.DANGER)
                    text(BUTTON_TEXT)
                }
            }
        }
    }

    companion object {
        private const val BUTTON_TEXT = "Delete"
    }
}