package core.logic

interface Dispatcher<Order, WorkReport> {
    fun registerOrder(orderId: OrderId, customerId: UserId, order: Order): DispatcherError?
    fun deleteOrder(orderId: OrderId): DispatcherError?
    fun getOrder(orderId: OrderId): Order?
    fun addExecutors(
        orderId: OrderId,
        executorsId: List<UserId>,
        unitWorksResult: (List<WorkReport?>) -> WorkReport?
    ): DispatcherError?

    fun delegateOrder(
        orderId: OrderId,
        srcId: UserId,
        dstId: List<UserId>,
        unitWorksResults: (List<WorkReport?>) -> WorkReport?
    ): DispatcherError?

    fun executeOrder(orderId: OrderId, executorId: UserId, report: WorkReport): DispatcherError?
    fun confirmExecution(orderId: OrderId, customerId: UserId, executorId: UserId): DispatcherError?
    fun confirmExecution(orderId: OrderId, executorId: UserId): DispatcherError?
    fun cancelExecution(orderId: OrderId, executorId: UserId): DispatcherError?
    fun cancelDelegation(orderId: OrderId, customerId: UserId, executorId: UserId): DispatcherError?
    fun cancelDelegation(orderId: OrderId, customerId: UserId): DispatcherError?
    fun getWorkResults(orderId: OrderId): WorkReport?
    fun getExecutors(orderId: OrderId): List<UserId>?
    fun getTheMostActiveRealExecutors(orderId: OrderId, count: Int): Map<UserId, Int>?
    fun getCustomerId(orderId: OrderId): UserId?
}