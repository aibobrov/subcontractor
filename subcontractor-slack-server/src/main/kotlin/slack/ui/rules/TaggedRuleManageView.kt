package slack.ui.rules

import com.slack.api.model.kotlin_extension.block.withBlocks
import com.slack.api.model.view.View
import com.slack.api.model.view.Views
import core.model.base.DelegationRule
import slack.model.SlackError
import slack.model.SlackManageMetadata
import slack.ui.base.SlackViewUIRepresentable
import slack.ui.base.UIConstant
import slack.ui.components.ErrorBlockView

class TaggedRuleManageView(val metadata: SlackManageMetadata, rules: List<DelegationRule>, errors: List<SlackError>) :
    SlackViewUIRepresentable {
    private val taggedViews = rules.map { TaggedRuleManageBlockView(it) }
    private val ruleActions = TaggedRuleManageActionBlockView()
    private val errorBlockView = ErrorBlockView(errors)
    override fun representIn(builder: View.ViewBuilder) {
        builder
            .type("modal")
            .title(Views.viewTitle { it.type("plain_text").text(VIEW_TITLE) })
            .close(Views.viewClose { it.type("plain_text").text(VIEW_CLOSE_BUTTON_TITLE) })
            .submit(Views.viewSubmit { it.type("plain_text").text(VIEW_CREATE_BUTTON_TITLE) })
            .privateMetadata(UIConstant.GSON.toJson(metadata))
            .callbackId(UIConstant.CallbackID.TAGGED_VIEW_SUBMISSION)
            .blocks(withBlocks {
                ruleActions.representIn(this)
                taggedViews.forEach { it.representIn(this) }
                errorBlockView.representIn(this)
            })
    }

    companion object {
        const val VIEW_TITLE = "Delegation rules"
        const val VIEW_CLOSE_BUTTON_TITLE = "Close"
        const val VIEW_CREATE_BUTTON_TITLE = "Done"
    }
}