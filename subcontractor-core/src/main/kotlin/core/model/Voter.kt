package core.model

import core.model.base.UserID

data class Voter(
    val id: UserID,
    val work: VoteWork
)

data class VoterInfo(
    val voter: Voter,
    val profileImageURL: String,
    val profileName: String
) {
    val id: UserID get() = voter.id
}
