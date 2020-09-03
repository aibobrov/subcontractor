package slack.service

import core.model.base.DelegationRule
import core.model.base.UserID

class SlackDelegationRuleRepositoryImpl : SlackDelegationRuleRepository {
    val storage = mutableMapOf<UserID, DelegationRule.Builder>()
    override fun getForUser(owner: UserID): DelegationRule.Builder? = storage[owner]

    override fun setBuilder(builder: DelegationRule.Builder) {
        storage[builder.owner] = builder
    }
}