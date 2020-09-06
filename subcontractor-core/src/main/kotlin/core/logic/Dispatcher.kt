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

    fun delegateOrder(
        workId: WorkId,
        srcId: UserId,
        dstId: UserId
    ): List<OrderId>

    fun executeOrder(workId: WorkId, orderId: OrderId, results: WorkResults?)
    fun executeOrder(workId: WorkId, userId: UserId, results: WorkResults?)
    fun cancelExecution(workId: WorkId, userId: UserId)
    fun cancelExecution(workId: WorkId, orderId: OrderId)
    fun cancelDelegation(workId: WorkId, orderId: OrderId)
    fun cancelDelegation(workId: WorkId, userId: UserId)
    fun getWorkResults(workId: WorkId): Map<UserId, WorkResults?>
    fun getTheMostActiveRealExecutors(workId: WorkId, count: Int): Map<UserId, Int>
    fun getCustomerId(workId: WorkId): UserId
    fun getExecutorsId(workId: WorkId): List<UserId>
}