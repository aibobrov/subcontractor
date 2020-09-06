package core.model.storage

import core.model.PollCreationTime
import core.model.PollVoter
import core.model.base.DelegationRule
import core.model.base.DelegationRuleID
import core.model.base.Poll
import core.model.base.PollID

class PollInfoStorageTestImpl : PollInfoStorage {

    private val creationTimes = mutableMapOf<PollID, MutableMap<PollVoter, PollCreationTime>>()
    private val polls = mutableMapOf<PollID, Poll>()
    private val delegations = mutableMapOf<PollVoter, MutableMap<DelegationRuleID, DelegationRule>>()

    override fun putPoll(pollId: PollID, poll: Poll) {
        polls[pollId] = poll
    }

    override fun getPoll(pollId: PollID): Poll? {
        return polls[pollId]
    }

    override fun addDelegationRule(rule: DelegationRule) {
        if (delegations[PollVoter(rule.owner)] == null) {
            delegations[PollVoter(rule.owner)] = mutableMapOf()
        }
        delegations[PollVoter(rule.owner)]?.put(rule.id, rule)
    }

    override fun deleteDelegationRule(voter: PollVoter, ruleId: DelegationRuleID) {
        delegations[voter]?.remove(ruleId)
    }

    override fun clearDelegationRules(voter: PollVoter) {
        delegations.remove(voter)
    }

    override fun getDelegationRules(voter: PollVoter): List<DelegationRule> {
        return delegations[voter]?.map {it -> it.value}?.toList() ?: listOf()
    }

    override fun putPollCreationTimes(pollId: PollID, times: MutableMap<PollVoter, PollCreationTime>) {
        creationTimes[pollId] = times
    }

    override fun putPollCreationTimes(pollId: PollID, voter: PollVoter, time: PollCreationTime) {
        creationTimes[pollId]?.put(voter, time)
    }

    override fun getPollCreationTimes(pollId: PollID): MutableMap<PollVoter, PollCreationTime> {
       creationTimes[pollId]?.let {
           return it
       }.run {
           return mutableMapOf()
       }
    }

}