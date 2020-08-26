package service

import core.model.VoteResults
import core.model.base.OptionID
import core.model.base.Poll
import core.model.base.PollID
import core.model.base.UserID
import core.model.errors.VotingError

interface VotingBusinessLogic {

    fun register(poll: Poll)

    fun get(pollID: PollID): Poll?

    fun vote(userID: UserID, pollID: PollID, optionID: OptionID)

    fun addVoters(pollID: PollID, usersId: List<UserID>)

    fun delegate(pollId: PollID, userId: UserID, toUserID: UserID): VotingError?

    fun cancelVote(pollID: PollID, userId: UserID)

    fun cancelDelegation(pollID: PollID, userId: UserID)

    fun voteResults(pollID: PollID): VoteResults
}
