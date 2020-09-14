package core.logic

interface Dispatcher<WorkResults> {
    fun registerWork(
        workId: WorkId,
        customerId: UserId,
        executorsId: List<UserId>
    ): Map<UserId, OrderId>

    fun deleteWork(workId: WorkId)

    fun delegateOrder(
        workId: WorkId,
        orderId: OrderId,
        srcId: UserId,
        dstId: UserId
    ): OrderId

    fun delegateAllOrders(
        workId: WorkId,
        srcId: UserId,
        dstId: UserId
    ): List<OrderId>

    fun executeOrder(workId: WorkId, orderId: OrderId, results: WorkResults?)
    fun executeAllOrders(workId: WorkId, userId: UserId, results: WorkResults?)
    fun cancelExecution(workId: WorkId, orderId: OrderId)
    fun cancelAllExecutions(workId: WorkId, userId: UserId)
    fun cancelDelegation(workId: WorkId, orderId: OrderId)
    fun cancelAllDelegations(workId: WorkId, userId: UserId)
    fun getWorkResults(workId: WorkId): Map<UserId, WorkResults?>
    fun getWorkResults(workId: WorkId, orderId: OrderId): WorkResults?
    fun getTheMostActiveRealExecutors(workId: WorkId, count: Int): Map<UserId, Int>
    fun getCustomerId(workId: WorkId): UserId
    fun getExecutorsId(workId: WorkId): List<UserId>
    fun getDelegations(workId: WorkId, userId: UserId): Map<OrderId, UserId>
    fun getOrdersId(workId: WorkId, userId: UserId): List<OrderId>
}