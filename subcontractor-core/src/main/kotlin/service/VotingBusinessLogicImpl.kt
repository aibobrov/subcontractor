package service

import core.logic.DataStorage
import core.logic.DispatcherError
import core.logic.DispatcherImpl
import core.model.PollResults
import core.model.VoteResults
import core.model.VoteWork
import core.model.Voter
import core.model.base.OptionID
import core.model.base.Poll
import core.model.base.PollID
import core.model.base.UserID
import core.model.errors.VotingError



class VotingBusinessLogicImpl(private val dispatcherStorage: DataStorage<Poll, PollResults>) : VotingBusinessLogic {

    private val dispatcher = DispatcherImpl(dispatcherStorage)

    override fun register(poll: Poll, votersId: List<UserID>) {
        dispatcher.registerWork(poll.id, poll, poll.author.id, votersId)
    }

    override fun getPoll(pollID: PollID): Poll? {
        return dispatcher.getWork(pollID)
    }

    override fun vote(pollID: PollID, userId: UserID, optionID: OptionID) {
        dispatcher.executeOrder(pollID, userId, PollResults(optionID))
    }

    override fun delegate(pollID: PollID, userId: UserID, toUserID: UserID): VotingError? {
        try {
            dispatcher.delegateOrder(pollID, userId, toUserID)
        } catch (error: DispatcherError.CycleFound) {
            return VotingError.CycleFound
        }
        return null
    }

    override fun cancelVote(pollID: PollID, userId: UserID) {
        dispatcher.cancelExecution(pollID, userId)
    }

    override fun cancelDelegation(pollID: PollID, userId: UserID) {
        dispatcher.cancelDelegation(pollID, userId)
    }

    override fun voteResults(pollID: PollID): VoteResults {

        val voteResults: MutableMap<OptionID, MutableList<Voter>> = mutableMapOf()

        val options = getPoll(pollID)?.options ?: return VoteResults(voteResults)

        for (option in options) {
            voteResults[option.id] = mutableListOf()
        }

        val results = dispatcher.getWorkResults(pollID)

        for (result in results) {
            val optionID = result.value?.result
            if (optionID != null) {
                if (voteResults[optionID] == null) {
                    voteResults[optionID] = mutableListOf(Voter(result.key, VoteWork.Vote(pollID, optionID)))
                } else {
                    voteResults[optionID]?.add(Voter(result.key, VoteWork.Vote(pollID, optionID)))
                }
            }
        }
        return VoteResults(voteResults)
    }
}
