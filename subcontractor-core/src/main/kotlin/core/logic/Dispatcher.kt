package core.logic

interface Dispatcher {
    fun createOrder(orderId: OrderId, userId: UserId, order: Order): DispatcherError
    fun deleteOrder(orderId: OrderId): DispatcherError
    fun getOrder(orderId: OrderId): Order?
    fun addExecutors(orderId: OrderId, executors: List<UserId>): DispatcherError
    fun delegateOrder(orderId: OrderId, srcId: UserId, dstId: List<UserId>): DispatcherError
    fun executeOrder(orderId: OrderId, executor: UserId, report: Report): DispatcherError
    fun confirmExecution(orderId: OrderId, executor: UserId): DispatcherError
    fun cancelExecution(orderId: OrderId, executor: UserId): DispatcherError
    fun getOrderTree(orderId: OrderId, userId: UserId): Node?
    fun getOrderTree(orderId: OrderId): Node?
    fun getConfirmReportsWithExecutors(orderId: OrderId): Map<UserId, Report>?
    fun getExecutors(orderId: OrderId): List<UserId>?
    fun getRealExecutors(orderId: OrderId): List<UserId>?
    fun getCustomer(orderId: OrderId): UserId?
}