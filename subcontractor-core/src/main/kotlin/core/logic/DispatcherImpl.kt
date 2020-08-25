package core.logic

open class DispatcherImpl<Order, WorkReport>(
    private val database: DataStorage<Order, WorkReport>
) : Dispatcher<Order, WorkReport> {

    override fun registerOrder(orderId: OrderId, customerId: UserId, order: Order): DispatcherError? {
        return database.addOrder(orderId, Worker(orderId), order)
    }

    override fun deleteOrder(orderId: OrderId): DispatcherError? {
        return database.deleteOrder(orderId)
    }

    override fun getOrder(orderId: OrderId): Order? {
        return database.getOrder(orderId)
    }

    override fun addExecutors(
        orderId: OrderId,
        executorsId: List<UserId>,
        unitWorksResult: (List<WorkReport?>) -> WorkReport?
    ): DispatcherError? {
        val customer: Customer<WorkReport> = database.getCustomer(orderId) ?: return DispatcherError.OrderNotFound
        for (id in executorsId) {
            val executor = Worker<WorkReport>(id)
            executor.addCustomer(customer)
            if (database.addWorker(orderId, executor) == null) {
                customer.addExecutor(executor)
            }
        }
        customer.setUnitWorksFunction(unitWorksResult)
        database.modifyCustomer(orderId, customer)
        return null
    }

    override fun delegateOrder(
        orderId: OrderId,
        srcId: UserId,
        dstId: List<UserId>,
        unitWorksResults: (List<WorkReport?>) -> WorkReport?
    ): DispatcherError? {
        val customer = database.getWorker(orderId, srcId) ?: return DispatcherError.WorkerNotFound
        for (id in dstId) {
            if (isContainsCycle(id, customer)) {
                return DispatcherError.CycleFound
            }
        }
        for (id in dstId) {
            val maybeExistWorker = database.getWorker(orderId, id)
            if (maybeExistWorker == null) {
                val newWorker = Worker(id, unitWorksResults)
                customer.addExecutor(newWorker)
                newWorker.addCustomer(customer)
                database.addWorker(orderId, newWorker)
            } else {
                customer.addExecutor(maybeExistWorker)
                maybeExistWorker.addCustomer(customer)
            }
        }
        customer.setUnitWorksFunction(unitWorksResults)
        database.modifyWorker(orderId, customer)
        return null
    }


    override fun executeOrder(orderId: OrderId, executorId: UserId, report: WorkReport): DispatcherError? {
        val worker = database.getWorker(orderId, executorId) ?: return DispatcherError.WorkerNotFound
        worker.executeWork(report)
        return database.modifyWorker(orderId, worker)
    }

    override fun confirmExecution(orderId: OrderId, customerId: UserId, executorId: UserId): DispatcherError? {
        val customer = database.getWorker(orderId, customerId) ?: return DispatcherError.OrderNotFound
        val executor = database.getWorker(orderId, executorId) ?: return DispatcherError.WorkerNotFound
        customer.confirmWorkReport(executor)
        database.modifyCustomer(orderId, customer)
        database.modifyWorker(orderId, executor)
        return null
    }

    override fun cancelExecution(orderId: OrderId, executorId: UserId): DispatcherError? {
        val executor = database.getWorker(orderId, executorId) ?: return DispatcherError.WorkerNotFound
        executor.deleteWork()
        return database.modifyWorker(orderId, executor)
    }


    override fun getExecutors(orderId: OrderId): List<UserId>? {
        val customer = database.getCustomer(orderId) ?: return null
        return customer.getExecutors().map { executor -> executor.userId }
    }


    override fun getCustomerId(orderId: OrderId): UserId? {
        return database.getCustomer(orderId)?.userId
    }

    private fun isContainsCycle(dstId: UserId, customer: Worker<WorkReport>): Boolean {
        if (dstId == customer.userId) {
            return true
        }
        for (parent in customer.getCustomers()) {
            if (parent is Worker<WorkReport> && parent.getCustomers().isNotEmpty()) {
                if (isContainsCycle(dstId, parent)) {
                    return true
                }
            }
        }
        return false
    }

    override fun getWorkResults(orderId: OrderId): WorkReport? {
        val customer = database.getCustomer(orderId) ?: return null
        return customer.getWorkResults()
    }

    override fun getTheMostActiveRealExecutors(orderId: OrderId, count: Int): Map<UserId, Int>? {
        val customer = database.getCustomer(orderId) ?: return null
        val realExecutors = customer.getRealExecutors()
        val countsOfWork = realExecutors.values.sorted()
        val topCounts = countsOfWork.subList(0, count)
        return realExecutors.filter { entry -> entry.value >= topCounts.last() }
    }

    override fun confirmExecution(orderId: OrderId, executorId: UserId): DispatcherError? {
        val executor = database.getWorker(orderId, executorId) ?: return DispatcherError.WorkerNotFound
        for (customer in executor.getCustomers()) {
            customer.confirmWorkReport(executor)
        }
        database.modifyWorker(orderId, executor)
        return null
    }

    private fun deleteExecutor(orderId: OrderId, customer: Worker<WorkReport>, executor: Worker<WorkReport>) {
        customer.deleteExecutor(executor)
        executor.deleteCustomer(customer)
        if (executor.getCustomers().isEmpty()) {
            database.deleteWorker(orderId, executor.userId)
        }
        database.modifyWorker(orderId, executor)
        database.modifyWorker(orderId, customer)
    }

    override fun cancelDelegation(orderId: OrderId, customerId: UserId, executorId: UserId): DispatcherError? {
        val customer = database.getWorker(orderId, customerId) ?: return DispatcherError.WorkerNotFound
        val executor = database.getWorker(orderId, executorId) ?: return DispatcherError.WorkerNotFound
        if (customer.getExecutors().contains(executor)) {
            deleteExecutor(orderId, customer, executor)
        }
        return null
    }

    override fun cancelDelegation(orderId: OrderId, customerId: UserId): DispatcherError? {
        val customer = database.getWorker(orderId, customerId) ?: return DispatcherError.WorkerNotFound
        for (executor in customer.getExecutors()) {
            deleteExecutor(orderId, customer, executor as Worker<WorkReport>)
        }
        return null
    }

}