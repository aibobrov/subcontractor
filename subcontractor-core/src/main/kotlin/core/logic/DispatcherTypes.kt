package core.logic

typealias UserId = String
typealias WorkId = String

data class OrderId(
    val customerId: UserId,
    val executorId: UserId
)
