package core.logic

interface DataStorage {
    fun addOrder(orderId: OrderId, userId: UserId, order: Order) : DispatcherError
    fun deleteOrder(orderId: OrderId) : DispatcherError
    fun getOrder(orderId: OrderId) : Order?
    fun addNode(orderId: OrderId, node : Node) : DispatcherError
    fun modifyNode(orderId: OrderId, node : Node) : DispatcherError
    fun deleteNode(orderId: OrderId, userId: UserId) : DispatcherError
    fun getNode(orderId: OrderId, userId: UserId) : Node?
    fun getCustomer(orderId: OrderId) : UserId?
}