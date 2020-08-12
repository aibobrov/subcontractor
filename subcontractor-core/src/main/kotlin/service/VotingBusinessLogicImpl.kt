package service

import core.logic.DataStorage
import core.model.VoteResults
import core.model.VoteWork
import core.model.Voter
import core.model.base.OptionID
import core.model.base.PollID
import core.model.base.UserID
import core.logic.DispatcherImpl



class VotingBusinessLogicImpl(private val storage : DataStorage) : VotingBusinessLogic {

    private val dispatcher = DispatcherImpl(storage)

    override fun vote(userID: UserID, pollID: PollID, optionID: OptionID) {
        dispatcher.executeOrder(pollID, userID, optionID)
    }

    override fun delegate(pollId : PollID, userId : UserID, toUserID: UserID) {
        dispatcher.delegateOrder(pollId, listOf(userId), toUserID)
    }

    override fun voteResults(pollID: PollID): VoteResults {
        val voteResults : MutableMap<OptionID, Voter> = mutableMapOf()
        val customerId : UserID = dispatcher.getCustomer(pollID) ?: return VoteResults(voteResults)
        val results : Map<UserID, OptionID> = dispatcher.getConfirmReportsWithUsers(pollID, customerId) ?: return VoteResults(voteResults)
        for (entry in results.entries) {
            voteResults[entry.key] = Voter(entry.value, VoteWork.Vote(pollID, entry.value))
        }
        return VoteResults(voteResults)
    }
}
