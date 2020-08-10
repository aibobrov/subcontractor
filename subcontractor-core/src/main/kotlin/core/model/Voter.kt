package core.model

import core.model.base.UserID

data class Voter(
    val userID: UserID,
    val work: VoteWork
)
