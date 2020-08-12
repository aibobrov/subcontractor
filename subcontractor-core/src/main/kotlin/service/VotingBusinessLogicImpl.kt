package service

import core.model.VoteResults
import core.model.VoteWork
import core.model.Voter
import core.model.base.OptionID
import core.model.base.PollID
import core.model.base.UserID
import core.logic.DispatcherImpl
import core.logic.DataStorageTestVersion


class VotingBusinessLogicImpl : VotingBusinessLogic {

    private val dispatcher = DispatcherImpl(DataStorageTestVersion())

    override fun vote(userID: UserID, pollID: PollID, optionID: OptionID) {
        dispatcher.executeOrder(pollID, userID, optionID)
    }

    override fun delegate(pollId : PollID, userId: UserID, toUserID: UserID) {
        dispatcher.delegateOrder(pollId, listOf(userId), toUserID)
    }

    override fun voteResults(pollID: PollID): VoteResults {
        val customerId : UserID? = dispatcher.getCustomer(pollID)
        val results : Map<UserID, OptionID>? = dispatcher.getConfirmReportsWithUsers(pollID, customerId)
        val voteResults : MutableMap<OptionID, Voter> = mutableMapOf()
        for (entry in results?.entries) {
            voteResults[entry.key] = Voter(results.values, VoteWork.Vote(pollID, results.values))
        }
        return VoteResults(voteResults())
    }
}
