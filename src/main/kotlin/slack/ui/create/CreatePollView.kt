package slack.ui.create

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import com.slack.api.model.kotlin_extension.block.withBlocks
import com.slack.api.model.view.View
import com.slack.api.model.view.Views.*
import core.model.PollOption
import core.model.PollType
import slack.model.PollAdvancedOption
import slack.model.SlackChannel
import slack.model.SlackError
import slack.model.SlackUser
import slack.model.SlackPollMetadata
import slack.ui.base.SlackViewUIRepresentable
import slack.ui.base.UIConstant
import slack.ui.components.ErrorBlockView
import slack.ui.components.FinishDateTimePickerBlockView
import slack.ui.components.StartDateTimePickerBlockView
import java.time.LocalDateTime

class CreatePollView(
    private val metadata: SlackPollMetadata,
    private val advancedSettings: PollAdvancedOption,
    currentPollType: PollType,
    options: List<PollOption>,
    startTime: LocalDateTime?,
    finishTime: LocalDateTime?,
    users: List<SlackUser>,
    channels: List<SlackChannel>,
    errors: List<SlackError> = listOf()
) : SlackViewUIRepresentable {
    private val createPollBlockView = CreatePollBlockView(currentPollType, options)
    private val audiencePickerBlockView = CreatePollAudiencePickerBlockView(users, channels)
    private val errorBlockView = ErrorBlockView(errors)
    private val startDateTimePickerBlockView =
        StartDateTimePickerBlockView(startTime)
    private val finishDateTimeBlockView =
        FinishDateTimePickerBlockView(finishTime)
    private val advancedSettingsBlockView = AdvancedSettingsBlockView(advancedSettings)

    override fun representIn(builder: View.ViewBuilder) {
        builder
            .type("modal")
            .title(viewTitle { it.type("plain_text").text(VIEW_TITLE) })
            .close(viewClose { it.type("plain_text").text(VIEW_CLOSE_BUTTON_TITLE) })
            .submit(viewSubmit { it.type("plain_text").text(VIEW_CREATE_BUTTON_TITLE) })
            .callbackId(UIConstant.CallbackID.CREATION_VIEW_SUBMISSION)
            .privateMetadata(UIConstant.GSON.toJson(metadata))
            .blocks(withBlocks {
                createPollBlockView.representIn(this)
                errorBlockView.representIn(this)
                divider()
                buildAdvancedSettings(this, advancedSettings)
                divider()
                audiencePickerBlockView.representIn(this)
            })
    }

    private fun buildAdvancedSettings(builder: LayoutBlockDsl, advancedSettings: PollAdvancedOption) {
        advancedSettingsBlockView.representIn(builder)
        if (advancedSettings.startDateTimeEnabled)
            startDateTimePickerBlockView.representIn(builder)
        if (advancedSettings.finishDateTimeEnabled)
            finishDateTimeBlockView.representIn(builder)
    }

    companion object {
        const val VIEW_TITLE = "Create poll"
        const val VIEW_CLOSE_BUTTON_TITLE = "Close"
        const val VIEW_CREATE_BUTTON_TITLE = "Create"
    }
}
