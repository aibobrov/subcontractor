package core.model.base

import core.model.PollType

interface Poll {
    val id: PollID

    val question: String

    val votingTime: VotingTime

    val type: PollType
}
