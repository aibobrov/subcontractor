package slack.ui.poll

import com.slack.api.model.Attachment
import core.model.PollAuthor
import core.model.base.Poll
import core.model.base.VotingTime
import slack.ui.base.SlackAttachmentUIRepresentable

class PreviewPollAttachment(
    poll: Poll,
    permalink: String
) : SlackAttachmentUIRepresentable {
    private val blockView = PreviewPollAttachmentBlockView(poll, permalink)

    override fun representIn(builder: Attachment.AttachmentBuilder) {
        builder
            .blocks(blockView.representation())
            .color("good")
    }
}
