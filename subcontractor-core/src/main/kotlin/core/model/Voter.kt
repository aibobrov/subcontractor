package core.model

import core.model.base.UserID

data class Voter(
    val id: UserID,
    val work: VoteWork
)
