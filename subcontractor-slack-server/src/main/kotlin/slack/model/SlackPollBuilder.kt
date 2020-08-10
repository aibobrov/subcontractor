package slack.model

import core.model.*
import core.model.base.Poll
import core.model.base.PollID
import core.model.base.PollTag
import core.model.base.VotingTime
import java.time.LocalDateTime

abstract class SlackPollBuilder(
    val id: PollID,
    val author: PollAuthor,
    var type: PollType
) {
    abstract var question: String
    abstract var description: String?
    abstract var options: List<PollOption>
    abstract var startTime: LocalDateTime?
    abstract var finishTime: LocalDateTime?
    abstract var tags: List<PollTag>
    abstract var isFinished: Boolean
    abstract var advancedOption: PollAdvancedOption

    fun with(builder: SlackPollBuilder) {
        question = builder.question
        description = builder.description
        options = builder.options
        startTime = builder.startTime
        finishTime = builder.finishTime
        tags = builder.tags
        isFinished = builder.isFinished
        advancedOption = builder.advancedOption
    }

    fun build(): Poll {
        return when (type) {
            PollType.SINGLE_CHOICE -> SingleChoicePoll(
                id,
                question,
                description,
                options,
                votingTime(this),
                isFinished,
                showResponses = advancedOption.showResponses,
                isAnonymous = advancedOption.isAnonymous,
                author = author,
                tags = tags
            )
            PollType.AGREE_DISAGREE -> AgreeDisagreePoll(
                id,
                question,
                votingTime(this),
                showResponses = advancedOption.showResponses,
                isAnonymous = advancedOption.isAnonymous,
                author = author,
                isFinished = isFinished
            )
        }
    }


    companion object {
        fun votingTime(builder: SlackPollBuilder): VotingTime {
            val startDateTimeEnabled = builder.advancedOption.startDateTimeEnabled
            val finishDateTimeEnabled = builder.advancedOption.finishDateTimeEnabled
            val startTime = builder.startTime ?: LocalDateTime.now()
            val finishTime = builder.finishTime ?: LocalDateTime.MAX
            return if (startDateTimeEnabled && finishDateTimeEnabled) {
                VotingTime.Ranged(startTime..finishTime)
            } else if (startDateTimeEnabled) {
                VotingTime.From(startTime)
            } else if (finishDateTimeEnabled) {
                VotingTime.UpTo(finishTime)
            } else {
                VotingTime.Unlimited
            }
        }
    }
}
