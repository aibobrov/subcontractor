package core.logic

class DataStorageTestVersion<Work, WorkResults> : DataStorage<Work, WorkResults> {

    private val works = mutableMapOf<WorkId, Work>()
    private val customers = mutableMapOf<WorkId, Customer>()
    private val orders = mutableMapOf<WorkId, MutableMap<OrderId, Order>>()
    private val workers = mutableMapOf<WorkId, MutableMap<UserId, Worker>>()
    private val reports = mutableMapOf<WorkId, MutableMap<OrderId, WorkResults?>>()


    override fun addWork(workId: WorkId, work: Work) {
        if (works[workId] != null) {
            throw DispatcherError.WorkerAlreadyExists
        }
        works[workId] = work
        orders[workId] = mutableMapOf()
        workers[workId] = mutableMapOf()
        reports[workId] = mutableMapOf()
    }

    override fun deleteWork(workId: WorkId) {
        if (works[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        customers.remove(workId)
        orders.remove(workId)
        workers.remove(workId)
        reports.remove(workId)
    }


    override fun addOrder(workId: WorkId, orderId: OrderId, order: Order) {
        if (works[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        orders[workId]?.put(orderId, order)
    }

    override fun getOrder(workId: WorkId, orderId: OrderId): Order {
        return orders[workId]?.get(orderId) ?: throw DispatcherError.OrderNotFound
    }

    override fun addWorker(workId: WorkId, worker: Worker) {
        if (workers[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        workers[workId]?.put(worker.userId, worker)
    }

    override fun modifyWorker(workId: WorkId, worker: Worker) {
        if (workers[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        if (workers[workId]?.get(worker.userId) == null) {
            throw DispatcherError.WorkerNotFound
        }
        workers[workId]?.put(worker.userId, worker)
    }

    override fun deleteWorker(workId: WorkId, workerId: UserId) {
        if (workers[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        if (workers[workId]?.get(workerId) == null) {
            throw DispatcherError.WorkerNotFound
        }
        workers[workId]?.remove(workerId)
    }

    override fun getWorker(workId: WorkId, workerId: UserId): Worker {
        return workers[workId]?.get(workerId) ?: throw DispatcherError.WorkNotFound
    }

    override fun getCustomer(workId: WorkId): Customer {
        return customers[workId] ?: throw DispatcherError.WorkNotFound
    }


    override fun addWorkResult(workId: core.logic.WorkId, orderId: OrderId, report: WorkResults?) {
        if (reports[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        if (orders[workId]?.get(orderId) == null) {
            throw DispatcherError.OrderNotFound
        }
        reports[workId]?.put(orderId, report)
    }

    override fun getWorkResult(workId: core.logic.WorkId, orderId: OrderId): WorkResults? {
        return reports[workId]?.get(orderId)
    }

    override fun modifyCustomer(workId: WorkId, customer: Customer) {
        if (customers[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        customers[workId] = customer
    }

    override fun addCustomer(workId: WorkId, customer: Customer) {
        if (works[workId] == null) {
            throw DispatcherError.WorkNotFound
        }
        customers[workId] = customer
    }

    override fun getWork(workId: WorkId): Work {
        return works[workId] ?: throw DispatcherError.WorkerNotFound
    }

}