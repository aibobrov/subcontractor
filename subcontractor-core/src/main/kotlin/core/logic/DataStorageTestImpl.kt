package core.logic

class DataStorageTestVersion<Order, WorkReport> : DataStorage<Order, WorkReport> {

    private val orders = mutableMapOf<OrderId, Order>()
    private val customers = mutableMapOf<OrderId, Customer<WorkReport>>()
    private val workers = mutableMapOf<OrderId, MutableMap<UserId, Worker<WorkReport>>>()

    override fun addOrder(orderId: OrderId, customer: Customer<WorkReport>, order: Order): DispatcherError? {
        if (orders.containsKey(orderId)) {
            DispatcherError.OrderAlreadyExists
        }
        orders[orderId] = order
        customers[orderId] = customer
        return null
    }

    override fun deleteOrder(orderId: OrderId): DispatcherError? {
        if (!orders.containsKey(orderId)) {
            return DispatcherError.OrderNotFound
        }
        orders.remove(orderId)
        customers.remove(orderId)
        return null
    }

    override fun getOrder(orderId: OrderId): Order? {
        return orders[orderId]
    }

    override fun modifyCustomer(orderId: OrderId, customer: Customer<WorkReport>): DispatcherError? {
        if (!orders.containsKey(orderId)) {
            return DispatcherError.OrderNotFound
        }
        customers[orderId] = customer
        return null
    }

    override fun getCustomer(orderId: OrderId): Customer<WorkReport>? {
        return customers[orderId]
    }


    override fun addWorker(orderId: OrderId, worker: Worker<WorkReport>): DispatcherError? {
        if (orders[orderId] == null) {
            return DispatcherError.OrderNotFound
        }
        if (workers[orderId]?.get(worker.userId) != null) {
            return DispatcherError.WorkerAlreadyExists
        }
        if (workers[orderId] == null) {
            workers[orderId] = mutableMapOf()
        }
        workers[orderId]?.set(worker.userId, worker)
        return null
    }

    override fun modifyWorker(orderId: OrderId, worker: Worker<WorkReport>): DispatcherError? {
        if (!orders.containsKey(orderId)) {
            return DispatcherError.OrderNotFound
        }
        if (workers[orderId]?.get(worker.userId) == null) {
            return DispatcherError.WorkerNotFound
        }
        workers[orderId]?.set(worker.userId, worker)
        return null
    }

    override fun deleteWorker(orderId: OrderId, workerId: UserId): DispatcherError? {
        if (!workers.containsKey(orderId)) {
            return DispatcherError.OrderNotFound
        }
        if (workers[orderId]?.get(workerId) == null) {
            return DispatcherError.WorkerNotFound
        }
        workers[orderId]?.remove(workerId)
        return null
    }

    override fun getWorker(orderId: OrderId, workerId: UserId): Worker<WorkReport>? {
        return workers[orderId]?.get(workerId)
    }


}