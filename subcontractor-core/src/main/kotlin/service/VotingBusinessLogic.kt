package service

import core.logic.OrderId
import core.model.VoteResults
import core.model.base.OptionID
import core.model.base.Poll
import core.model.base.PollID
import core.model.base.UserID
import core.model.errors.VotingError

interface VotingBusinessLogic {

    fun register(poll: Poll, votersId: List<UserID>)

    fun getPoll(pollID: PollID): Poll?

    fun vote(pollID: PollID, userId: UserID, optionID: OptionID)

    fun delegate(pollID: PollID, userId: UserID, toUserID: UserID): VotingError?

    fun cancelVote(pollID: PollID, userId: UserID)

    fun cancelDelegation(pollID: PollID, userId: UserID)

    fun voteResults(pollID: PollID): VoteResults
}
