package core.logic

class DataStorageTestImpl<WorkResults> : DataStorage<WorkResults> {

    private val customers = mutableMapOf<WorkId, Customer>()
    private val orders = mutableMapOf<WorkId, MutableMap<OrderId, Order>>()
    private val workers = mutableMapOf<WorkId, MutableMap<UserId, Worker>>()
    private val reports = mutableMapOf<WorkId, MutableMap<OrderId, WorkResults?>>()


    override fun addWork(workId: WorkId, customer: Customer) {
        if (customers[workId] != null) {
            throw DispatcherError.WorkAlreadyExists
        }
        customers[workId] = customer
        orders[workId] = mutableMapOf()
        workers[workId] = mutableMapOf()
        reports[workId] = mutableMapOf()
    }

    override fun deleteWork(workId: WorkId) {
        if (customers[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        customers.remove(workId)
        orders.remove(workId)
        workers.remove(workId)
        reports.remove(workId)
    }


    override fun addOrder(workId: WorkId, orderId: OrderId, order: Order) {
        if (customers[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        orders[workId]?.put(orderId, order)
    }

    override fun getOrder(workId: WorkId, orderId: OrderId): Order {
        return orders[workId]?.get(orderId) ?: throw DispatcherError.OrderNotFound
    }

    override fun addWorker(workId: WorkId, worker: Worker) {
        if (customers[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        workers[workId]?.put(worker.userId, worker)
    }

    override fun modifyWorker(workId: WorkId, worker: Worker) {
        if (customers[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        if (workers[workId]?.get(worker.userId) == null) {
            throw DispatcherError.WorkerNotFound
        }
        workers[workId]?.put(worker.userId, worker)
    }

    override fun deleteWorker(workId: WorkId, workerId: UserId) {
        if (customers[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        if (workers[workId]?.get(workerId) == null) {
            throw DispatcherError.WorkerNotFound
        }
        workers[workId]?.remove(workerId)
    }

    override fun getWorker(workId: WorkId, workerId: UserId): Worker {
        return workers[workId]?.get(workerId) ?: throw DispatcherError.WorkerNotFound
    }

    override fun getCustomer(workId: WorkId): Customer {
        return customers[workId] ?: throw DispatcherError.WorkNotFound
    }


    override fun addWorkResult(workId: WorkId, orderId: OrderId, report: WorkResults?) {
        if (reports[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        if (orders[workId]?.get(orderId) == null) {
            throw DispatcherError.OrderNotFound
        }
        reports[workId]?.put(orderId, report)
    }

    override fun getWorkResult(workId: WorkId, orderId: OrderId): WorkResults? {
        return reports[workId]?.get(orderId)
    }

    override fun modifyCustomer(workId: WorkId, customer: Customer) {
        if (customers[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        customers[workId] = customer
    }

}