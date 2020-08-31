package core.logic

data class WorkReport(
    val reportPath: List<OrderId>,
    val resultId: OrderId
)