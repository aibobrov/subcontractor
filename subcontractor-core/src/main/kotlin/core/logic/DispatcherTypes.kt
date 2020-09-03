package core.logic

import kotlinx.serialization.Serializable

typealias UserId = String
typealias WorkId = String

@Serializable
data class OrderId(
    val customerId: UserId,
    val executorId: UserId
)
