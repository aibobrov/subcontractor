package core.logic

interface Customer<WorkReport> {

    val userId: UserId

    fun setUnitWorksFunction(unitWorks: (List<WorkReport?>) -> WorkReport?)

    fun addExecutor(executor: Executor<WorkReport>)

    fun addExecutors(executors: List<Executor<WorkReport>>)

    fun deleteExecutor(executor: Executor<WorkReport>)

    fun getExecutors(): List<Executor<WorkReport>>

    fun confirmWorkReport(executor: Executor<WorkReport>)

    fun getWorkResults(): WorkReport?

    fun getRealExecutors(): Map<UserId, Int>
}