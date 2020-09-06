package core.model.storage

import core.model.PollCreationTime
import core.model.PollVoter
import core.model.base.DelegationRule
import core.model.base.DelegationRuleID
import core.model.base.Poll
import core.model.base.PollID

interface PollInfoStorage {
    fun putPoll(pollId: PollID, poll: Poll)
    fun getPoll(pollId: PollID): Poll?
    fun addDelegationRule(rule: DelegationRule)
    fun deleteDelegationRule(voter: PollVoter, ruleId: DelegationRuleID)
    fun clearDelegationRules(voter: PollVoter)
    fun getDelegationRules(voter: PollVoter): List<DelegationRule>
    fun putPollCreationTimes(pollId: PollID, times: MutableMap<PollVoter, PollCreationTime>)
    fun putPollCreationTimes(pollId: PollID, voter: PollVoter, time: PollCreationTime)
    fun getPollCreationTimes(pollId: PollID): MutableMap<PollVoter, PollCreationTime>
}