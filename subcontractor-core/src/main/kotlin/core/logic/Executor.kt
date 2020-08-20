package core.logic


interface Executor<WorkReport> {

    val userId: UserId

    fun addCustomers(customers: List<Customer<WorkReport>>)

    fun addCustomer(customer: Customer<WorkReport>)

    fun executeWork(report: WorkReport)

    fun deleteWork()

    fun getWorkReport(): WorkReport?

    fun getCustomers(): List<Customer<WorkReport>>?

    fun getWorkResults(): WorkReport?

    fun getRealExecutors(): Map<UserId, Int>

    fun hasConfirmWorkReport(customer: Customer<WorkReport>): Boolean

    fun setReportsConfirm(customer: Customer<WorkReport>, isConfirm: Boolean)

}