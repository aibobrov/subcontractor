package core.model

import core.model.base.Poll
import core.model.base.PollID
import core.model.base.PollTag
import core.model.base.VotingTime

class PollBuilder(
    val id: PollID,
    val author: PollAuthor,
    var type: PollType
) {
    var question: String = ""
    var description: String? = null
    var options: List<PollOption> = listOf()
    var votingTime: VotingTime = VotingTime.Unlimited
    var tags: List<PollTag> = listOf()
    var isFinished: Boolean = false

    fun build(): Poll {
        return when (type) {
            PollType.SINGLE_CHOICE -> SingleChoicePoll(
                id, question, description, options, votingTime, tags, isFinished, author
            )
            PollType.AGREE_DISAGREE -> TODO()
        }
    }
}
