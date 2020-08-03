package core.model

import core.model.base.*

data class SingleChoicePoll(
    override val id: PollID,
    override val question: Text,
    val authorId: UserID,
    val description: String?,
    val options: List<PollOption>,
    override val votingTime: VotingTime,
    val tags: List<PollTag> = listOf(),
    val isFinished: Boolean = false
) : Poll
