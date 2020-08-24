package core.logic

interface DataStorage<Order, WorkReport> {
    fun addOrder(orderId: OrderId, customer: Customer<WorkReport>, order: Order): DispatcherError?
    fun deleteOrder(orderId: OrderId): DispatcherError?
    fun getOrder(orderId: OrderId): Order?
    fun addWorker(orderId: OrderId, worker: Worker<WorkReport>): DispatcherError?
    fun modifyWorker(orderId: OrderId, worker: Worker<WorkReport>): DispatcherError?
    fun deleteWorker(orderId: OrderId, workerId: UserId): DispatcherError?
    fun getWorker(orderId: OrderId, workerId: UserId): Worker<WorkReport>?
    fun modifyCustomer(orderId: OrderId, customer: Customer<WorkReport>): DispatcherError?
    fun getCustomer(orderId: OrderId): Customer<WorkReport>?
}