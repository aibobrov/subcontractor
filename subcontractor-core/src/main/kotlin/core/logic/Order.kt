package core.logic

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: OrderId,
    val ordersChain: OrdersChain,
    val creationTime: DispatcherTime
)