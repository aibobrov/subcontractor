package slack.model

import core.model.PollAuthor
import core.model.PollOption
import core.model.PollType
import core.model.base.PollTag
import java.time.LocalDateTime

class SlackImmutableOptionPollBuilder(
    id: String,
    author: PollAuthor,
    type: PollType,
    private val immutableOptions: List<PollOption>
) : SlackPollBuilder(id, author, type) {

    override var question: String = ""
    override var description: String? = null
    override var options: List<PollOption>
        get() = immutableOptions
        set(value) {}

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

    constructor(builder: SlackPollBuilder, immutableOptions: List<PollOption>) : this(
        builder.id,
        builder.author,
        builder.type,
        immutableOptions
    ) {
        question = builder.question
        description = builder.description
        startTime = builder.startTime
        finishTime = builder.finishTime
        tags = builder.tags
        isFinished = builder.isFinished
        advancedOption = builder.advancedOption
    }
}
