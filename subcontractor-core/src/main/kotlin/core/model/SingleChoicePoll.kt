package core.model

import core.model.base.Poll
import core.model.base.PollID
import core.model.base.PollTag
import core.model.base.VotingTime

data class SingleChoicePoll(
    override val id: PollID,
    override val question: String,
    val description: String?,
    override val options: List<PollOption>,
    override val votingTime: VotingTime,
    override val isFinished: Boolean,
    override val showResponses: Boolean,
    override val isAnonymous: Boolean,
    override val author: PollAuthor,
    override val audience: PollAudience,
    override val tags: List<PollTag>
) : Poll {
    override val type: PollType
        get() = PollType.SINGLE_CHOICE
}
