package slack.ui.rules

import com.slack.api.model.kotlin_extension.block.withBlocks
import com.slack.api.model.view.View
import com.slack.api.model.view.Views
import slack.model.SlackError
import slack.model.SlackManageMetadata
import slack.ui.base.SlackViewUIRepresentable
import slack.ui.base.UIConstant
import slack.ui.components.ErrorBlockView

class CreateDelegationRuleView(val metadata: SlackManageMetadata, errors: List<SlackError>) : SlackViewUIRepresentable {
    private val ruleBlockView = CreateDelegationRuleBlockView()
    private val errorBlockView = ErrorBlockView(errors)
    override fun representIn(builder: View.ViewBuilder) {
        builder
            .type("modal")
            .title(Views.viewTitle { it.type("plain_text").text(VIEW_TITLE) })
            .close(Views.viewClose { it.type("plain_text").text(VIEW_CLOSE_BUTTON_TITLE) })
            .submit(Views.viewSubmit { it.type("plain_text").text(VIEW_CREATE_BUTTON_TITLE) })
            .privateMetadata(UIConstant.GSON.toJson(metadata))
            .callbackId(UIConstant.CallbackID.CREATE_DELEGATION_RULE_SUBMISSION)
            .blocks(withBlocks {
                ruleBlockView.representIn(this)
                errorBlockView.representIn(this)
            })
    }

    companion object {
        private const val VIEW_TITLE = "Create a delegation rule"
        private const val VIEW_CLOSE_BUTTON_TITLE = "Close"
        private const val VIEW_CREATE_BUTTON_TITLE = "Create"
    }
}