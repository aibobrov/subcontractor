package slack.ui.create

import com.slack.api.model.view.View
import com.slack.api.model.view.Views.*
import core.model.PollOption
import slack.model.SlackPollMetadata
import slack.ui.base.SlackViewUIRepresentable
import slack.ui.base.UIConstant

class EditOptionsPollView(
    private val metadata: SlackPollMetadata,
    choices: List<PollOption>
) : SlackViewUIRepresentable {
    private val blockView = EditOptionsPollBlockView(choices)

    override fun representIn(builder: View.ViewBuilder) {
        builder
            .type("modal")
            .submit(viewSubmit { it.text(VIEW_CREATE_BUTTON_TITLE).type("plain_text") })
            .close(viewClose { it.text(VIEW_CLOSE_BUTTON_TITLE).type("plain_text") })
            .title(viewTitle { it.text(VIEW_TITLE).type("plain_text") })
            .callbackId(UIConstant.CallbackID.ADD_OPTION_VIEW_SUBMISSION)
            .privateMetadata(UIConstant.GSON.toJson(metadata))
            .blocks(blockView.representation())
    }

    companion object {
        const val VIEW_TITLE = "Add options"
        const val VIEW_CLOSE_BUTTON_TITLE = "Close"
        const val VIEW_CREATE_BUTTON_TITLE = "Submit"
    }
}
