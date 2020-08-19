package core.model

import core.model.base.*

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
    val tags: List<PollTag> = listOf()
) : Poll {
    override val type: PollType
        get() = PollType.SINGLE_CHOICE
}
