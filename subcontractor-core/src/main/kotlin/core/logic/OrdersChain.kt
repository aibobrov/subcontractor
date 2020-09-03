package core.logic

import kotlinx.serialization.Serializable

@Serializable
class OrdersChain(
    val orders: List<OrderId>,
    val times: List<DispatcherTime>
) {
    fun getExecutorsId(): List<UserId> {
        return orders.map { it.executorId }
    }
}