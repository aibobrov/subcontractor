package slack.service

import core.model.base.DelegationRule
import core.model.base.UserID

interface SlackDelegationRuleRepository {
    fun getForUser(owner: UserID): DelegationRule.Builder?

    fun setBuilder(builder: DelegationRule.Builder)
}