package core.logic

open class DispatcherImpl(private val database: DataStorage) : Dispatcher {

    protected fun unitReports(reports: List<Report>): Report? {
        return if (reports.isEmpty()) {
            null
        } else {
            reports[0]
        }
    }

    private fun isContainsLoop(id: UserId, node: Node): Boolean {
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

    private fun deleteNodesRecursively(orderId: OrderId, node: Node) {
        database.deleteNode(orderId, node.getUserId())
        for (child in node.getChildren()) {
            deleteNodesRecursively(orderId, child)
        }
    }


    override fun createOrder(orderId: OrderId, userId: UserId, order: Order): DispatcherError {
        return database.addOrder(orderId, userId, order)
    }

    override fun deleteOrder(orderId: OrderId): DispatcherError {
        return database.deleteOrder(orderId)
    }

    override fun getOrder(orderId: OrderId): Order? {
        return database.getOrder(orderId)
    }

    override fun addExecutors(orderId: OrderId, executors: List<UserId>): DispatcherError {
        val root: Node = database.getRoot(orderId) ?: return DispatcherError.ORDER_DOES_NOT_EXIST
        if (root.getChildren().isNotEmpty()) {
            return DispatcherError.ORDER_ALREADY_HAS_EXECUTORS
        }
        for (id in executors) {
            val node = Node(id)
            node.addParent(root)
            if (database.addNode(orderId, node) == DispatcherError.EMPTY_ERROR) {
                root.addChild(node)
            }
        }
        return DispatcherError.EMPTY_ERROR
    }

    override fun delegateOrder(orderId: OrderId, srcId: UserId, dstId: List<UserId>): DispatcherError {
        val node: Node = database.getNode(orderId, srcId) ?: return  DispatcherError.NODE_DOES_NOT_EXIST
        for (id in dstId) {
            if (isContainsLoop(id, node)) {
                return DispatcherError.LOOP_IS_FOUND
            }
        }
        for (id in dstId) {
            val maybeExist = database.getNode(orderId, id)
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
        return DispatcherError.EMPTY_ERROR
    }


    override fun executeOrder(orderId: OrderId, executor: UserId, report: Report): DispatcherError {
        val node = database.getNode(orderId, executor) ?: return DispatcherError.NODE_DOES_NOT_EXIST

        if (node.getChildren().isEmpty()) {
            node.setReport(report)
            return database.modifyNode(orderId, node)
        }

        for (child in node.getChildren()) {
            deleteNodesRecursively(orderId, node)
        }

        return DispatcherError.EMPTY_ERROR
    }

    override fun confirmExecution(orderId: OrderId, executor: UserId): DispatcherError {
        val node = database.getNode(orderId, executor) ?: return DispatcherError.NODE_DOES_NOT_EXIST
        node.setConfirm(true)
        return database.modifyNode(orderId, node)
    }

    override fun cancelExecution(orderId: OrderId, executor: UserId): DispatcherError {
        val node = database.getNode(orderId, executor) ?: return DispatcherError.NODE_DOES_NOT_EXIST
        node.setReport(null)
        node.setConfirm(false)
        return database.modifyNode(orderId, node)
    }

    private fun getConfirmReport(orderId: OrderId, node: Node): Report? {
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

    private fun getRealExecutors(orderId: OrderId, node: Node): List<UserId>? {
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

    override fun getOrderTree(orderId: OrderId): Node? {
        return database.getRoot(orderId)
    }

    override fun getOrderTree(orderId: OrderId, userId: UserId): Node? {
        return database.getNode(orderId, userId)
    }

    override fun getCustomer(orderId: OrderId): UserId? {
        return database.getCustomer(orderId)
    }

}