package core.model.base

import kotlinx.serialization.Serializable

@Serializable
data class DelegationRule(val id: DelegationRuleID, val owner: UserID, val tags: Set<PollTag>, val toUserID: UserID) {
    constructor(builder: Builder) : this(builder.id, builder.owner, builder.tags, builder.toUserID!!)

    class Builder(
        val id: DelegationRuleID,
        var owner: UserID,
        var tags: Set<PollTag> = setOf(),
        var toUserID: UserID? = null
    ) {
        fun build(): DelegationRule = DelegationRule(this)
    }
}