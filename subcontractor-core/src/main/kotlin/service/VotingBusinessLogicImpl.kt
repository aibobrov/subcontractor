package service

import core.logic.DataStorage
import core.logic.DispatcherError
import core.logic.DispatcherImpl
import core.model.*
import core.model.base.*
import core.model.errors.VotingError
import core.model.storage.PollInfoStorage


class VotingBusinessLogicImpl(
        dispatcherStorage: DataStorage<PollResults>,
        private val pollInfoStorage: PollInfoStorage
) : VotingBusinessLogic {

    private val dispatcher = DispatcherImpl(dispatcherStorage)

    override fun register(poll: Poll, votersId: List<UserID>) {
        dispatcher.registerWork(poll.id, poll.author.id, votersId)
    }

    override fun vote(pollID: PollID, userId: UserID, optionID: OptionID) {
        try {
            dispatcher.executeAllOrders(pollID, userId, PollResults(optionID))
        } catch (error: DispatcherError.DelegationIsNotRelevant) { }
    }

    override fun getVote(pollID: PollID, userId: UserID): PollResults? {
        val ordersId = dispatcher.getOrdersId(pollID, userId)
        if (ordersId.isNotEmpty()) {
            return dispatcher.getWorkResults(pollID, ordersId[0])
        }
        return null
    }

    override fun getDelegation(pollID: PollID, userId: UserID): UserID? {
        val delegations = dispatcher.getDelegations(pollID, userId)
        return if (delegations.isEmpty()) null else delegations.values.first()
    }


    @Synchronized
    override fun delegate(pollID: PollID, userId: UserID, toUserID: UserID): VotingError? {
        try {
            dispatcher.delegateAllOrders(pollID, userId, toUserID)
        } catch (error: DispatcherError.CycleFound) {
            return VotingError.CycleFound
        }
        return null
    }

    override fun cancelVote(pollID: PollID, userId: UserID) {
        try {
            dispatcher.cancelAllExecutions(pollID, userId)
        } catch (error: DispatcherError.DelegationIsNotRelevant) { }
    }

    override fun cancelDelegation(pollID: PollID, userId: UserID) {
        dispatcher.cancelAllDelegations(pollID, userId)
    }

    override fun voteResults(pollID: PollID): VoteResults {

        val voteResults: MutableMap<OptionID, MutableList<Voter>> = mutableMapOf()

        val options = pollInfoStorage.getPoll(pollID)?.options ?: return VoteResults(voteResults)

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

    override fun delegationRules(userId: UserID): List<DelegationRule> {
        return pollInfoStorage.getDelegationRules(PollVoter(userId))
    }

    override fun deleteDelegationRule(userID: UserID, delegationRuleID: String) {
        pollInfoStorage.deleteDelegationRule(PollVoter(userID), delegationRuleID)
    }

    override fun clearDelegationRules(forUserID: UserID) {
        pollInfoStorage.clearDelegationRules(PollVoter(forUserID))
    }

    override fun addDelegationRule(delegationRule: DelegationRule) {
        pollInfoStorage.addDelegationRule(delegationRule)
    }

    @Synchronized
    override fun applyDelegationRules(userId: UserID, poll: Poll): UserID {
        val rules = delegationRules(userId)
        for (rule in rules) {
            if (rule.tags.toSet() == poll.tags.toSet()) {
                delegate(poll.id, userId, rule.toUserID) ?: return applyDelegationRules(rule.toUserID, poll)
            }
        }
        return userId
    }
}

