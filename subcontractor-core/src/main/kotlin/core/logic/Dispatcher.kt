package core.logic

interface Dispatcher<Order, Report> {
    fun registerOrder(orderId: OrderId, userId: UserId, order: Order): DispatcherError?
    fun deleteOrder(orderId: OrderId): DispatcherError?
    fun getOrder(orderId: OrderId): Order?
    fun addExecutors(orderId: OrderId, executors: List<UserId>): DispatcherError?
    fun delegateOrder(orderId: OrderId, srcId: UserId, dstId: List<UserId>): DispatcherError?
    fun executeOrder(orderId: OrderId, executor: UserId, report: Report): DispatcherError?
    fun confirmExecution(orderId: OrderId, executor: UserId): DispatcherError?
    fun cancelExecution(orderId: OrderId, executor: UserId): DispatcherError?
    fun getOrderTree(orderId: OrderId, userId: UserId): Node<Report>?
    fun getOrderTree(orderId: OrderId): Node<Report>?
    fun getConfirmReportsWithExecutors(orderId: OrderId): Map<UserId, Report>?
    fun getExecutors(orderId: OrderId): List<UserId>?
    fun getRealExecutors(orderId: OrderId): List<UserId>?
    fun getCustomer(orderId: OrderId): UserId?
}