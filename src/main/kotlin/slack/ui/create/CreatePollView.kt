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
import slack.ui.base.SlackViewUIRepresentable
import java.time.LocalDate
import java.time.LocalDateTime

class CreatePollView(
    private val metadata: CreationMetadata,
    private val advancedSettings: PollAdvancedOption,
    currentPollType: PollType,
    options: List<PollOption>,
    initialStartTime: LocalDateTime?,
    initialFinishTime: LocalDateTime?,
    users: List<SlackUser>,
    channels: List<SlackChannel>,
    errors: List<SlackError> = listOf()
) : SlackViewUIRepresentable {
    private val createPollBlockView = CreatePollBlockView(currentPollType, options)
    private val audiencePickerBlockView = CreatePollAudiencePickerBlockView(users, channels)
    private val errorBlockView = ErrorBlockView(errors)
    private val startDateTimePickerBlockView = StartDateTimePickerBlockView(
        LocalDateTime.now(),
        initialStartTime?.toLocalTime(),
        initialStartTime?.toLocalDate() ?: LocalDate.now()
    )
    private val finishDateTimeBlockView = FinishDateTimeBlockView(
        LocalDateTime.now(),
        initialFinishTime?.toLocalTime(),
        initialFinishTime?.toLocalDate() ?: LocalDate.now()
    )
    private val advancedSettingsBlockView = AdvancedSettingsBlockView(advancedSettings)

    override fun representIn(builder: View.ViewBuilder) {
        builder
            .type("modal")
            .title(viewTitle { it.type("plain_text").text(VIEW_TITLE) })
            .close(viewClose { it.type("plain_text").text(VIEW_CLOSE_BUTTON_TITLE) })
            .submit(viewSubmit { it.type("plain_text").text(VIEW_CREATE_BUTTON_TITLE) })
            .callbackId(CreationConstants.CallbackID.CREATION_VIEW_SUBMISSION)
            .privateMetadata(CreationConstants.GSON.toJson(metadata))
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
