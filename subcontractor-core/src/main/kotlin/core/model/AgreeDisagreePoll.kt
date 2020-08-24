package core.model

import core.model.base.Poll
import core.model.base.PollID
import core.model.base.VotingTime
import java.util.*

data class AgreeDisagreePoll(
    override val id: PollID,
    override val question: String,
    override val votingTime: VotingTime,
    override val author: PollAuthor,
    override val showResponses: Boolean,
    override val isAnonymous: Boolean,
    override val isFinished: Boolean,
    override val audience: PollAudience
) : Poll {
    override val type: PollType = PollType.AGREE_DISAGREE

    override val options = listOf(
        PollOption(UUID.randomUUID().toString(), "Agree"),
        PollOption(UUID.randomUUID().toString(), "Disagree")
    )

    companion object {
        val OPTIONS = listOf(
            PollOption("1", "Agree"),
            PollOption("2", "Disagree")
        )
    }
}
