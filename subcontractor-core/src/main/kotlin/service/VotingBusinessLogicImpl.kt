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


class VotingBusinessLogicImpl(val storage: DataStorage<Poll, PollResults>) : VotingBusinessLogic {

    private val dispatcher = DispatcherImpl(storage)

    override fun registerPoll(poll: Poll) {
        dispatcher.registerOrder(poll.id, poll.author.id, poll)
    }

    override fun getPoll(pollID: PollID): Poll? {
        return dispatcher.getOrder(pollID)
    }

    override fun vote(userID: UserID, pollID: PollID, optionID: OptionID) {
        dispatcher.executeOrder(pollID, userID, PollResults.Option(optionID))
        dispatcher.confirmExecution(pollID, userID)
    }

    override fun addVoters(pollID: PollID, usersId: List<UserID>) {
        dispatcher.addExecutors(pollID, usersId) { resultsList: List<PollResults?> ->
            PollResults.OptionsList(
                resultsList
            )
        }
    }

    override fun delegate(pollId: PollID, userId: UserID, toUserID: UserID): VotingError? {
        val maybeError =
            dispatcher.delegateOrder(pollId, userId, listOf(toUserID)) { results: List<PollResults?> -> results.last() }
        if (maybeError == DispatcherError.CycleFound) {
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

        val executors = dispatcher.getExecutors(pollID) ?: return VoteResults(voteResults)


        val results: PollResults.OptionsList =
            dispatcher.getWorkResults(pollID) as PollResults.OptionsList? ?: return VoteResults(voteResults)

        if (results.list.size != executors.size) {
            return VoteResults(voteResults)
        }
        for (i in results.list.indices) {
            val option = results.list[i] as PollResults.Option?
            val optionID = option?.result
            val executorId = executors[i]
            if (optionID != null) {
                if (voteResults[optionID] == null) {
                    voteResults[optionID] = mutableListOf(Voter(executorId, VoteWork.Vote(pollID, optionID)))
                } else {
                    voteResults[optionID]?.add(Voter(executorId, VoteWork.Vote(pollID, optionID)))
                }
            }
        }
        return VoteResults(voteResults)
    }
}
