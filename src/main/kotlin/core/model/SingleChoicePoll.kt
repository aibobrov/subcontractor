package core.model

import core.model.base.*

data class PollAuthor(val id: String, val name: String)

data class SingleChoicePoll(
    override val id: PollID,
    override val question: String,
    val description: String?,
    val options: List<PollOption>,
    override val votingTime: VotingTime,
    val tags: List<PollTag> = listOf(),
    val isFinished: Boolean = false,
    val author: PollAuthor
) : Poll {
    override val type: PollType
        get() = PollType.SINGLE_CHOICE
}
