package slack.ui.poll

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.base.Poll
import slack.model.SlackUser
import slack.ui.base.SlackBlockUIRepresentable
import slack.ui.base.UIConstant

class PollContextBlockView(private val poll: Poll) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        buildContext(builder, poll)
    }

    private fun buildContext(builder: LayoutBlockDsl, poll: Poll) {
        builder.context {
            val owner = "Owner: ${UIConstant.Text.userText(SlackUser(poll.author.id))}"
            val closes = "🕔  Closes: ${UIConstant.Text.votingTime(poll.votingTime)}"
            val anonymous = UIConstant.Text.anonymousText(poll.isAnonymous)
            markdownText("$owner | $closes | $anonymous")
        }
    }
}
