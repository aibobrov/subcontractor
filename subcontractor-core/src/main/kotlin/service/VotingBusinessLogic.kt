package service

import core.model.VoteResults
import core.model.base.OptionID
import core.model.base.PollID
import core.model.base.UserID

interface VotingBusinessLogic {
    fun vote(userID: UserID, pollID: PollID, optionID: OptionID)

    fun delegate(pollId: PollID, userID: UserID, toUserID: UserID)

    fun voteResults(pollID: PollID): VoteResults
}
