package core.logic

class Worker<WorkReport>(
    override val userId: UserId,
    private var unitWorks: (List<WorkReport?>) -> WorkReport? = { null }
) : Customer<WorkReport>, Executor<WorkReport> {

    private val customers = mutableListOf<Customer<WorkReport>>()
    private val executors = mutableListOf<Executor<WorkReport>>()
    private var report: WorkReport? = null
    private val reportsConfirms = mutableMapOf<Customer<WorkReport>, Boolean>()

    override fun setUnitWorksFunction(unitWorks: (List<WorkReport?>) -> WorkReport?) {
        this.unitWorks = unitWorks
    }

    override fun addCustomers(customers: List<Customer<WorkReport>>) {
        this.customers.addAll(customers)
        setNotConfirmReports()
    }

    override fun addCustomer(customer: Customer<WorkReport>) {
        customers.add(customer)
        reportsConfirms[customer] = false
    }

    override fun executeWork(report: WorkReport) {
        this.report = report
        setNotConfirmReports()
    }

    override fun deleteWork() {
        this.report = null
        setNotConfirmReports()
    }


    override fun getCustomers(): List<Customer<WorkReport>> {
        return customers
    }

    override fun getWorkReport(): WorkReport? {
        return report
    }

    override fun hasConfirmWorkReport(customer: Customer<WorkReport>): Boolean {
        if (reportsConfirms[customer] == null) {
            return false
        }
        return report != null && reportsConfirms[customer] != null && reportsConfirms[customer] == true
    }

    override fun addExecutor(executor: Executor<WorkReport>) {
        executors.add(executor)
    }

    override fun addExecutors(executors: List<Executor<WorkReport>>) {
        this.executors.addAll(executors)
    }

    override fun getExecutors(): List<Executor<WorkReport>> {
        return executors
    }

    override fun confirmWorkReport(executor: Executor<WorkReport>) {
        for (ex in executors) {
            if (ex == executor) {
                executor.getWorkReport()?.let { executor.setReportsConfirm(this, true) }
            }
        }
    }

    override fun getWorkResults(): WorkReport? {
        if (executors.isEmpty()) {
            return null
        }
        val workReports = mutableListOf<WorkReport?>()
        for (executor in executors) {
            if (executor.hasConfirmWorkReport(this)) {
                workReports.add(executor.getWorkReport())
            } else {
                workReports.add(executor.getWorkResults())
            }
        }
        return unitWorks(workReports)
    }

    override fun getRealExecutors(): Map<UserId, Int> {
        val realExecutors = mutableMapOf<UserId, Int>()
        for (executor in executors) {
            if (executor.hasConfirmWorkReport(this)) {
                realExecutors[executor.userId]?.plus(1).run {
                    realExecutors[executor.userId] = 1
                }
            } else {
                realExecutors.putAll(executor.getRealExecutors())
            }
        }
        return realExecutors
    }

    private fun setNotConfirmReports() {
        for (customer in customers) {
            reportsConfirms[customer] = false
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other is Worker<*>) && other.userId == userId
    }

    override fun hashCode(): Int {
        return userId.hashCode()
    }


    override fun setReportsConfirm(customer: Customer<WorkReport>, isConfirm: Boolean) {
        if (customers.contains(customer) && report != null) {
            reportsConfirms[customer] = isConfirm
        }
    }
}