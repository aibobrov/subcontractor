package service

import core.model.VoteResults
import core.model.base.OptionID
import core.model.base.Poll
import core.model.base.PollID
import core.model.base.UserID

interface VotingBusinessLogic {

    fun registerPoll(pollID: PollID, author: UserID, poll: Poll)

    fun getPoll(pollID: PollID): Poll?

    fun vote(userID: UserID, pollID: PollID, optionID: OptionID)

    fun addVoters(pollID: PollID, usersId : List<UserID>)

    fun delegate(pollId: PollID, userId: UserID, toUserID: UserID)

    fun voteResults(pollID: PollID): VoteResults
}
