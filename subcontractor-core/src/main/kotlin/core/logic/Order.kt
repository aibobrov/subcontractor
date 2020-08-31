package core.logic

data class Order(
    val id: OrderId,
    val ordersChain: OrdersChain,
    val creationTime: DispatcherTime
)