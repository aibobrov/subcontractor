package service

import core.model.VoteResults
import core.model.VoteWork
import core.model.Voter
import core.model.base.OptionID
import core.model.base.PollID
import core.model.base.UserID
import core.model.storage.LiquidPollRepository

// TODO: reimplement all logic
class VotingBusinessLogicImpl(
    private val pollRepository: LiquidPollRepository
) : VotingBusinessLogic {
    private val votesStorage: MutableMap<PollID, VoteResults> = mutableMapOf()
    private val usersToWork: MutableMap<UserID, Voter> = mutableMapOf()
    private val delegationStorage: MutableMap<UserID, UserID> = mutableMapOf()

    private fun mutableMapVoters(pollID: PollID): MutableMap<OptionID, Set<Voter>> {
        val options = emptyResults(pollID)
        val immutableVoteResults = votesStorage[pollID] ?: options
        return immutableVoteResults.toMutableMap()
    }

    // TODO: edge cases/cleanup/refactor
    override fun vote(userID: UserID, pollID: PollID, optionID: OptionID) {
        fun deletePrevWork() { // remove old vote
            val oldVoter = usersToWork.remove(userID)
            if (oldVoter != null) {
                check(oldVoter.work is VoteWork.Vote) { "Delegation is not supported yet" }
                val vote = oldVoter.work
                val voteResults = mutableMapVoters(pollID)
                val voters = voteResults[vote.optionId] ?: setOf()
                voteResults[vote.optionId] = voters.minus(oldVoter)
                votesStorage[vote.pollID] = VoteResults(voteResults)
            }
        }

        fun addVoteWork() {// add vote
            val mutableResults = mutableMapVoters(pollID)
            val voters = mutableResults[optionID] ?: setOf()
            val newVoter = Voter(userID, VoteWork.Vote(pollID, optionID))
            mutableResults[optionID] = voters.plus(newVoter)
            val newVoteResults = VoteResults(mutableResults)
            votesStorage[pollID] = newVoteResults
            usersToWork[userID] = newVoter
        }

        deletePrevWork()

        addVoteWork()
    }

    override fun delegate(userID: UserID, toUserID: UserID) {
        // TODO: edge cases
        delegationStorage[userID] = toUserID
    }

    private fun emptyResults(pollID: PollID): VoteResults {
        return VoteResults.empty(pollRepository.get(pollID)?.options ?: listOf())
    }


    override fun voteResults(pollID: PollID): VoteResults {
        // TODO: edge cases
        return votesStorage[pollID] ?: emptyResults(pollID)
    }
}
