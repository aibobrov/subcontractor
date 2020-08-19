package core.logic

open class DispatcherImpl<Order, Report>(private val database: DataStorage<Order, Report>,
     private val unitReports : (List<Report>) -> Report?) : Dispatcher<Order, Report> {

    private fun isContainsLoop(id: UserId, node: Node<Report>): Boolean {
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

    private fun deleteNodesRecursively(orderId: OrderId, node: Node<Report>) {
        database.deleteNode(orderId, node.getUserId())
        for (child in node.getChildren()) {
            deleteNodesRecursively(orderId, child)
        }
    }


    override fun registerOrder(orderId: OrderId, userId: UserId, order: Order): DispatcherError? {
        return database.addOrder(orderId, userId, order)
    }

    override fun deleteOrder(orderId: OrderId): DispatcherError? {
        return database.deleteOrder(orderId)
    }

    override fun getOrder(orderId: OrderId): Order? {
        return database.getOrder(orderId)
    }

    override fun addExecutors(orderId: OrderId, executors: List<UserId>): DispatcherError? {
        val root: Node<Report> = database.getRoot(orderId) ?: return DispatcherError.OrderNotFound
        if (root.getChildren().isNotEmpty()) {
            return DispatcherError.OrderAlreadyHasExecutors
        }
        for (id in executors) {
            val node = Node<Report>(id)
            node.addParent(root)
            if (database.addNode(orderId, node) == null) {
                root.addChild(node)
            }
        }
        return null
    }

    override fun delegateOrder(orderId: OrderId, srcId: UserId, dstId: List<UserId>): DispatcherError? {
        val node: Node<Report> = database.getNode(orderId, srcId) ?: return  DispatcherError.NodeNotFound
        for (id in dstId) {
            if (isContainsLoop(id, node)) {
                return DispatcherError.CycleFound
            }
        }
        for (id in dstId) {
            val maybeExist = database.getNode(orderId, id)
            if (maybeExist == null) {
                val newNode = Node<Report>(id)
                node.addChild(newNode)
                newNode.addParent(node)
                database.addNode(orderId, node)
            } else {
                node.addChild(maybeExist)
                maybeExist.addParent(node)
            }
        }
        database.modifyNode(orderId, node)
        return null
    }


    override fun executeOrder(orderId: OrderId, executor: UserId, report: Report): DispatcherError? {
        val node = database.getNode(orderId, executor) ?: return DispatcherError.NodeNotFound

        if (node.getChildren().isEmpty()) {
            node.setReport(report)
            return database.modifyNode(orderId, node)
        }

        for (child in node.getChildren()) {
            deleteNodesRecursively(orderId, node)
        }

        return null
    }

    override fun confirmExecution(orderId: OrderId, executor: UserId): DispatcherError? {
        val node = database.getNode(orderId, executor) ?: return DispatcherError.NodeNotFound
        node.setConfirm(true)
        return database.modifyNode(orderId, node)
    }

    override fun cancelExecution(orderId: OrderId, executor: UserId): DispatcherError? {
        val node = database.getNode(orderId, executor) ?: return DispatcherError.NodeNotFound
        node.setReport(null)
        node.setConfirm(false)
        return database.modifyNode(orderId, node)
    }

    private fun getConfirmReport(orderId: OrderId, node: Node<Report>): Report? {
        return if (node.isConfirmReport()) {
            node.getReport()
        } else {
            val reports = mutableListOf<Report>()
            for (child in node.getChildren()) {
                getConfirmReport(orderId, child)?.let{reports.add(it)}
            }
            unitReports(reports)
        }
    }

    override fun getConfirmReportsWithExecutors(orderId: OrderId): Map<UserId, Report>? {
        val root = database.getRoot(orderId) ?: return null
        val reports = mutableMapOf<UserId, Report>()
        for (child in root.getChildren()) {
            getConfirmReport(orderId, child)?.let{ reports[child.getUserId()] = it }
        }
        return reports
    }

    override fun getExecutors(orderId: OrderId): List<UserId>? {
        val root = database.getRoot(orderId) ?: return null
        val executors = mutableListOf<UserId>()
        for (child in root.getChildren()) {
            executors.add(child.getUserId())
        }
        return executors
    }

    private fun getRealExecutors(orderId: OrderId, node: Node<Report>): List<UserId>? {
        val realExecutors = mutableListOf<UserId>()
        for (child in node.getChildren()) {
            if (child.isConfirmReport()) {
                realExecutors.add(child.getUserId())
            } else {
                getRealExecutors(orderId, child)?.let { realExecutors.addAll(it) }
            }
        }
        return realExecutors
    }

    override fun getRealExecutors(orderId: OrderId): List<UserId>? {
        val root = database.getRoot(orderId) ?: return null
        return getRealExecutors(orderId, root)
    }

    override fun getOrderTree(orderId: OrderId): Node<Report>? {
        return database.getRoot(orderId)
    }

    override fun getOrderTree(orderId: OrderId, userId: UserId): Node<Report>? {
        return database.getNode(orderId, userId)
    }

    override fun getCustomer(orderId: OrderId): UserId? {
        return database.getCustomer(orderId)
    }

}