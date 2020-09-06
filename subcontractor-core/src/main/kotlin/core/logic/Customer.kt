package core.logic

import kotlinx.serialization.Serializable

@Serializable
class Customer(
    val userId: UserId,
    private val orders: Map<UserId, OrderId>
) {
    private var workReports: MutableMap<UserId, WorkReport?> =
        orders.map { it.key to null }.toMap().toMutableMap()

    private var ordersResults: MutableMap<UserId, MutableMap<OrderId, WorkReport>?> =
        orders.map { it.key to null }.toMap().toMutableMap()

    fun setWorkReport(userId: UserId, report: WorkReport) {
        workReports[userId] = report
        for (orderId in report.reportPath) {
            ordersResults[orderId.executorId]?.put(orderId, report)
        }
    }

    fun getRealExecutors(): Map<UserId, UserId> {
        val realExecutors = mutableMapOf<UserId, UserId>()
        for (report in workReports.values) {
            if (report != null && report.reportPath.isEmpty()) {
                val executorId = report.reportPath.first().customerId
                val realExecutorId = report.reportPath.first().executorId
                realExecutors[executorId] = realExecutorId
            }
        }
        return realExecutors
    }

    fun getWorkResults(): Map<UserId, WorkReport?> {
        return workReports
    }

    fun getWorkResults(userId: UserId): Map<OrderId, WorkReport>? {
        return ordersResults[userId]
    }

    fun getExecutorsId(): List<UserId> {
        return orders.keys.toList()
    }
}