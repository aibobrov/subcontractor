package core.logic

open class DispatcherImpl(private val database : Database) : Dispatcher {

    protected fun unitReports(reports : List<Report>) : Report? {
        return if (reports.isEmpty()) {
            null
        } else {
            reports[0]
        }
    }

    private fun isContainsLoop(id: UserId, node : Node) : Boolean {
        if (id == node.getUserId()) {
            return true
        }
        for (parent in node.getParents()) {
            if (isContainsLoop(id, parent)) {
                return true
            }
        }
        return false
    }

    private fun deleteNodesRecursively(orderId: OrderId, node : Node) {
        database.deleteNode(orderId, node.getUserId())
        for (child in node.getChildren()) {
            deleteNodesRecursively(orderId, child)
        }
    }


    override fun createOrder(userId: UserId, order : Order) : DispatcherResponse {
        return database.addOrder(userId, order)
    }

    override fun deleteOrder(orderId: OrderId) : DispatcherResponse {
        return database.deleteOrder(orderId)
    }

    override fun getOrder(orderId: OrderId): Order? {
        return database.getOrder(orderId)
    }

    override fun delegateOrder(srcId: UserId, dstId: List<UserId>, orderId: OrderId): DispatcherResponse {
        val node : Node = database.getNode(orderId, srcId) ?: return DispatcherResponse(false, "this node doesn't exist")
        for (id in dstId) {
            if (isContainsLoop(id, node)) {
                return DispatcherResponse(false, "loop is found")
            }
        }
        for (id in dstId) {
            val maybeExist = database.getNode(orderId, node.getUserId())
            if (maybeExist == null) {
                val newNode = Node(id)
                node.addChild(newNode)
                newNode.addParent(node)
                database.addNode(orderId, node)
            } else {
                node.addChild(maybeExist)
                maybeExist.addParent(node)
            }
        }
        database.modifyNode(orderId, node)
        return DispatcherResponse(true, "order delegated successfully")
    }

    override fun executeOrder(orderId: OrderId, executor: UserId, report: Report): DispatcherResponse {
        val node = database.getNode(orderId, executor) ?: return DispatcherResponse(false, "this node doesn't exist")

        if (node.getReport() == null) {
            node.setReport(report)
            return database.modifyNode(orderId, node)
        }

        for (child in node.getChildren()) {
            deleteNodesRecursively(orderId, node)
        }

        return DispatcherResponse(true, "order executed successfully")
    }

    override fun confirmExecution(orderId: OrderId, executor: UserId, report: Report): DispatcherResponse {
        val node = database.getNode(orderId, executor) ?: return DispatcherResponse(false, "order delegated successfully")
        node.setConfirm(true)
        return database.modifyNode(orderId, node)
    }

    override fun cancelExecution(orderId: OrderId, executor: UserId): DispatcherResponse {
        val node = database.getNode(orderId, executor) ?: return DispatcherResponse(false, "order delegated successfully")
        node.setReport(null)
        node.setConfirm(false)
        return database.modifyNode(orderId, node)
    }

    override fun getConfirmReports(orderId : OrderId, userId: UserId) : List<Report>? {
        database.getNode(orderId, userId) ?: return null
        val map = getConfirmReportsWithUsers(orderId, userId)
        return map?.values?.toList()
    }

    override fun getConfirmReportsWithUsers(orderId : OrderId, userId: UserId) : Map<UserId, Report>? {
        val node = database.getNode(orderId, userId) ?: return null
        val reports = mutableMapOf<UserId, Report>()
        for (child in node.getChildren()) {
            if (node.isConfirmReport()) {
                node.getReport()?.let {reports[userId] = it }
            } else {
                val childReports = getConfirmReports(orderId, child.getUserId())
                val report = childReports?.let { unitReports(it) }
                report?.let { reports[userId] = it }
            }
        }
        return reports
    }

    override fun getExecutors(orderId: OrderId, userId: UserId) : List<UserId>? {
        val node = database.getNode(orderId, userId) ?: return null
        val executors = mutableListOf<UserId>()
        for (child in node.getChildren()) {
            executors.add(child.getUserId())
        }
        return executors
    }

    override fun getRealExecutors(orderId: OrderId, userId: UserId): List<UserId>? {
        val node = database.getNode(orderId, userId) ?: return null
        val realExecutors = mutableListOf<UserId>()
        for (child in node.getChildren()) {
            if (child.isConfirmReport()) {
                realExecutors.add(child.getUserId())
            } else {
                getRealExecutors(orderId, child.getUserId())?.let { realExecutors.addAll(it) }
            }
        }
        return realExecutors
    }

    override fun getReportsTree(orderId: OrderId, userId: UserId): Node? {
        return database.getNode(orderId, userId)
    }

    override fun getCustomer(orderId : OrderId) : UserId? {
        return database.getCustomer(orderId)
    }

}