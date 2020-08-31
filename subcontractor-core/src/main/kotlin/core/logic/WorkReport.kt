package core.logic

import kotlinx.serialization.Serializable

@Serializable
data class WorkReport(
    val reportPath: List<OrderId>,
    val resultId: OrderId
)