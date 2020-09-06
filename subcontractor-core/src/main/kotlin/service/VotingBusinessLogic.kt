package service

import core.model.VoteResults
import core.model.base.*
import core.model.errors.VotingError

interface VotingBusinessLogic {

    fun register(poll: Poll, votersId: List<UserID>)

    fun vote(pollID: PollID, userId: UserID, optionID: OptionID)

    fun delegate(pollID: PollID, userId: UserID, toUserID: UserID): VotingError?

    fun cancelVote(pollID: PollID, userId: UserID)

    fun cancelDelegation(pollID: PollID, userId: UserID)

    fun voteResults(pollID: PollID): VoteResults

    fun delegationRules(userId: UserID): List<DelegationRule>

    fun deleteDelegationRule(userID: UserID, delegationRuleID: String)

    fun clearDelegationRules(forUserID: UserID)

    fun addDelegationRule(delegationRule: DelegationRule)

    fun applyDelegationRules(userId: UserID, poll: Poll): UserID
}
