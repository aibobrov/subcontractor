package core.logic

class DataStorageTestVersion<Order, Report> : DataStorage<Order, Report> {

    private val customers = mutableMapOf<OrderId, UserId>()
    private val orders = mutableMapOf<OrderId, Order>()
    private val nodes = mutableMapOf<OrderId, MutableMap<UserId, Node<Report>>>()
    private val rootId = ""

    override fun addOrder(orderId: OrderId, userId: UserId, order: Order): DispatcherError? {
        if (orders.containsKey(orderId)) {
            return DispatcherError.OrderAlreadyExists
        }
        orders[orderId] = order
        customers[orderId] = userId
        nodes[orderId] = mutableMapOf(rootId to Node<Report>(rootId))
        return null
    }

    override fun deleteOrder(orderId: OrderId): DispatcherError?{
        if (!orders.containsKey(orderId)) {
            return DispatcherError.OrderNotFound
        }
        orders.remove(orderId)
        nodes.remove(orderId)
        customers.remove(orderId)
        return null
    }

    override fun getOrder(orderId: OrderId): Order? {
        return orders[orderId]
    }

    override fun addNode(orderId: OrderId, node: Node<Report>): DispatcherError? {
        if (!nodes.containsKey(orderId)) {
            return DispatcherError.OrderNotFound
        }
        if (nodes[orderId]?.get(node.getUserId()) != null) {
            return DispatcherError.NodeAlreadyExists
        }
        nodes[orderId]?.set(node.getUserId(), node)
        return null
    }

    override fun modifyNode(orderId: OrderId, node: Node<Report>): DispatcherError? {
        if (!nodes.containsKey(orderId)) {
            return DispatcherError.OrderNotFound
        }
        if (nodes[orderId]?.get(node.getUserId()) == null) {
            return DispatcherError.NodeNotFound
        }
        nodes[orderId]?.set(node.getUserId(), node)
        return null
    }

    override fun getNode(orderId: OrderId, userId: UserId): Node<Report>? {
        return nodes[orderId]?.get(userId)
    }

    override fun deleteNode(orderId: OrderId, userId: UserId): DispatcherError? {
        if (nodes[orderId]?.get(userId) == null) {
            return DispatcherError.NodeNotFound
        }
        nodes[orderId]?.remove(userId)
        return null
    }

    override fun getCustomer(orderId: OrderId): UserId? {
        return customers[orderId]
    }

    override fun getRoot(orderId: OrderId): Node<Report>? {
        return nodes[orderId]?.get(rootId)
    }


}