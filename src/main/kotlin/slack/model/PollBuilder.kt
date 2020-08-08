package slack.model

import core.model.PollAuthor
import core.model.PollOption
import core.model.PollType
import core.model.SingleChoicePoll
import core.model.base.Poll
import core.model.base.PollID
import core.model.base.PollTag
import core.model.base.VotingTime
import java.time.LocalDateTime

class PollBuilder(
    val id: PollID,
    val author: PollAuthor,
    var type: PollType
) {
    var question: String = ""
    var description: String? = null
    var options: List<PollOption> = listOf()
    var startTime: LocalDateTime? = null
    var finishTime: LocalDateTime? = null
    var tags: List<PollTag> = listOf()
    var isFinished: Boolean = false
    var advancedOption: PollAdvancedOption = PollAdvancedOption(
        showResponses = true,
        startDateTimeEnabled = false,
        finishDateTimeEnabled = false,
        isAnonymous = false
    )

    fun build(): Poll {
        return when (type) {
            PollType.SINGLE_CHOICE -> SingleChoicePoll(
                id,
                question,
                description,
                options,
                votingTime(this),
                tags,
                isFinished,
                showResponses = advancedOption.showResponses,
                isAnonymous = advancedOption.isAnonymous,
                author = author
            )
            PollType.AGREE_DISAGREE -> TODO()
        }
    }

    companion object {
        fun votingTime(builder: PollBuilder): VotingTime {
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
