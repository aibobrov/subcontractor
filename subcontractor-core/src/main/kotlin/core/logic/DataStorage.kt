package core.logic

interface DataStorage<WorkResults> {
    fun addWork(workId: WorkId, customer: Customer)
    fun deleteWork(workId: WorkId)
    fun addOrder(workId: WorkId, orderId: OrderId, order: Order)
    fun getOrder(workId: WorkId, orderId: OrderId): Order
    fun addWorker(workId: WorkId, worker: Worker)
    fun modifyWorker(workId: WorkId, worker: Worker)
    fun deleteWorker(workId: WorkId, workerId: UserId)
    fun getWorker(workId: WorkId, workerId: UserId): Worker
    fun modifyCustomer(workId: WorkId, customer: Customer)
    fun getCustomer(workId: WorkId): Customer
    fun addWorkResult(workId: WorkId, orderId: OrderId, report: WorkResults?)
    fun getWorkResult(workId: WorkId, orderId: OrderId): WorkResults?
}