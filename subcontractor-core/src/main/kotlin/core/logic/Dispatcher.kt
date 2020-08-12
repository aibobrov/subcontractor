package core.logic

interface Dispatcher {
    fun createOrder(userId : UserId, order : Order) : DispatcherResponse
    fun deleteOrder(orderId: OrderId) : DispatcherResponse
    fun getOrder(orderId: OrderId) : Order?
    fun delegateOrder(srcId : UserId, dstId : List<UserId>, orderId : OrderId) : DispatcherResponse
    fun executeOrder(orderId : OrderId, executor : UserId, report : Report) : DispatcherResponse
    fun confirmExecution(orderId : OrderId, executor: UserId, report: Report) : DispatcherResponse
    fun cancelExecution(orderId : OrderId, executor : UserId) : DispatcherResponse
    fun getReportsTree(orderId : OrderId, userId: UserId) : Node?
    fun getConfirmReports(orderId : OrderId, userId: UserId) : List<Report>?
    fun getConfirmReportsWithUsers(orderId : OrderId, userId: UserId) :  Map<UserId, Report>?
    fun getExecutors(orderId: OrderId, userId: UserId) : List<UserId>?
    fun getRealExecutors(orderId: OrderId, userId: UserId): List<UserId>?
    fun getCustomer(orderId: OrderId) : UserId?
}