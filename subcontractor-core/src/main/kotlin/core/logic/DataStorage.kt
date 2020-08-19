package core.logic

interface DataStorage<Order, Report> {
    fun addOrder(orderId: OrderId, userId: UserId, order: Order): DispatcherError?
    fun deleteOrder(orderId: OrderId): DispatcherError?
    fun getOrder(orderId: OrderId): Order?
    fun addNode(orderId: OrderId, node: Node<Report>): DispatcherError?
    fun modifyNode(orderId: OrderId, node: Node<Report>): DispatcherError?
    fun deleteNode(orderId: OrderId, userId: UserId): DispatcherError?
    fun getNode(orderId: OrderId, userId: UserId): Node<Report>?
    fun getCustomer(orderId: OrderId): UserId?
    fun getRoot(orderId: OrderId): Node<Report>?
}