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
        dispatcher.confirmExecution(pollID, userID)
    }

    override fun addVoters(pollID: PollID, usersId: List<UserID>) {
        dispatcher.addExecutors(pollID, usersId)
    }

    override fun delegate(pollId : PollID, userId : UserID, toUserID: UserID) {
        dispatcher.delegateOrder(pollId, userId, listOf(toUserID))
    }

    override fun voteResults(pollID: PollID): VoteResults {
        val voteResults: MutableMap<OptionID, MutableList<Voter>> = mutableMapOf()
        val results: Map<UserID, OptionID> = dispatcher.getConfirmReportsWithExecutors(pollID) ?: return VoteResults(voteResults)
        for (entry in results.entries) {
            if (voteResults[entry.value] == null) {
                voteResults[entry.value] = mutableListOf(Voter(entry.key, VoteWork.Vote(pollID, entry.value)))
            } else {
                voteResults[entry.value]?.add(Voter(entry.key, VoteWork.Vote(pollID, entry.value)))
            }
        }
        return VoteResults(voteResults)
    }
}
