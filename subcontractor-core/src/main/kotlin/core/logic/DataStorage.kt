package core.logic

interface DataStorage {
    fun addOrder(userId: UserId, order: Order) : DispatcherResponse
    fun deleteOrder(orderId: OrderId) :DispatcherResponse
    fun getOrder(orderId: OrderId) : Order?
    fun addNode(orderId: OrderId, node : Node) : DispatcherResponse
    fun modifyNode(orderId: OrderId, node : Node) : DispatcherResponse
    fun deleteNode(orderId: OrderId, userId: UserId) : DispatcherResponse
    fun getNode(orderId: OrderId, userId: UserId) : Node?
    fun getCustomer(orderId: OrderId) : UserId?
}