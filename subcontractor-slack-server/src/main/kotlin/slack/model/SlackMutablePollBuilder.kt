package slack.model

import core.model.PollAuthor
import core.model.PollOption
import core.model.PollType
import core.model.base.PollTag
import java.time.LocalDateTime

class SlackMutablePollBuilder(
    id: String,
    author: PollAuthor,
    type: PollType
) : SlackPollBuilder(id, author, type) {
    override var question: String = ""
    override var description: String? = null
    override var options: List<PollOption> = listOf()
    override var startTime: LocalDateTime? = null
    override var finishTime: LocalDateTime? = null
    override var tags: List<PollTag> = listOf()
    override var isFinished: Boolean = false
    override var advancedOption: PollAdvancedOption = PollAdvancedOption(
        showResponses = true,
        startDateTimeEnabled = false,
        finishDateTimeEnabled = false,
        isAnonymous = false
    )
}
