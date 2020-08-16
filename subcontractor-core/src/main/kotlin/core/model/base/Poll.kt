package core.model.base

import core.model.PollAuthor
import core.model.PollOption
import core.model.PollType
import core.model.SlackAudience

interface Poll {
    val id: PollID

    val question: String

    val votingTime: VotingTime

    val type: PollType

    val author: PollAuthor

    val showResponses: Boolean

    val isAnonymous: Boolean

    val isFinished: Boolean

    val options: List<PollOption>

    val audience: SlackAudience
}
