package core.model.base

interface Poll {
    val id: PollID

    val question: Text

    val votingTime: VotingTime
}
