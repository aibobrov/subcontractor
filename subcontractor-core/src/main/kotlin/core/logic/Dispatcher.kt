package core.logic

interface Dispatcher {
    fun createOrder(orderId: OrderId, userId : UserId, order : Order) : DispatcherError
    fun deleteOrder(orderId: OrderId) : DispatcherError
    fun getOrder(orderId: OrderId) : Order?
    fun delegateOrder(srcId : UserId, dstId : List<UserId>, orderId : OrderId) : DispatcherError
    fun executeOrder(orderId : OrderId, executor : UserId, report : Report) : DispatcherError
    fun confirmExecution(orderId : OrderId, executor: UserId, report: Report) : DispatcherError
    fun cancelExecution(orderId : OrderId, executor : UserId) : DispatcherError
    fun getReportsTree(orderId : OrderId, userId: UserId) : Node?
    fun getConfirmReports(orderId : OrderId, userId: UserId) : List<Report>?
    fun getConfirmReportsWithUsers(orderId : OrderId, userId: UserId) :  Map<UserId, Report>?
    fun getExecutors(orderId: OrderId, userId: UserId) : List<UserId>?
    fun getRealExecutors(orderId: OrderId, userId: UserId): List<UserId>?
    fun getCustomer(orderId: OrderId) : UserId?
}