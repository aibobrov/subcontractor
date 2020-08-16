package core.logic

class DataStorageTestVersion : DataStorage {

    private val customers = mutableMapOf<OrderId, UserId>()
    private val orders = mutableMapOf<OrderId, Order>()
    private val nodes = mutableMapOf<OrderId, MutableMap<UserId, Node>>()
    private val rootId = ""

    override fun addOrder(orderId: OrderId, userId: UserId, order: Order): DispatcherError {
        if (orders.containsKey(orderId)) {
            return DispatcherError.ORDER_ALREADY_EXIST
        }
        orders[orderId] = order
        customers[orderId] = userId
        nodes[orderId] = mutableMapOf(rootId to Node(rootId))
        return DispatcherError.EMPTY_ERROR
    }

    override fun deleteOrder(orderId: OrderId): DispatcherError {
        if (!orders.containsKey(orderId)) {
            return DispatcherError.ORDER_DOES_NOT_EXIST
        }
        orders.remove(orderId)
        nodes.remove(orderId)
        customers.remove(orderId)
        return DispatcherError.EMPTY_ERROR
    }

    override fun getOrder(orderId: OrderId): Order? {
        return orders[orderId]
    }

    override fun addNode(orderId: OrderId, node: Node): DispatcherError {
        if (!nodes.containsKey(orderId)) {
            return DispatcherError.ORDER_DOES_NOT_EXIST
        }
        if (nodes[orderId]?.get(node.getUserId()) != null) {
            return DispatcherError.NODE_AlREADY_EXIST
        }
        nodes[orderId]?.set(node.getUserId(), node)
        return DispatcherError.EMPTY_ERROR
    }

    override fun modifyNode(orderId: OrderId, node: Node): DispatcherError {
        if (!nodes.containsKey(orderId)) {
            return DispatcherError.ORDER_DOES_NOT_EXIST
        }
        if (nodes[orderId]?.get(node.getUserId()) == null) {
            return DispatcherError.NODE_DOES_NOT_EXIST
        }
        nodes[orderId]?.set(node.getUserId(), node)
        return DispatcherError.EMPTY_ERROR
    }

    override fun getNode(orderId: OrderId, userId: UserId): Node? {
        return nodes[orderId]?.get(userId)
    }

    override fun deleteNode(orderId: OrderId, userId: UserId): DispatcherError {
        if (nodes[orderId]?.get(userId) == null) {
            return DispatcherError.NODE_DOES_NOT_EXIST
        }
        nodes[orderId]?.remove(userId)
        return DispatcherError.EMPTY_ERROR
    }

    override fun getCustomer(orderId: OrderId): UserId? {
        return customers[orderId]
    }

    override fun getRoot(orderId: OrderId): Node? {
        return nodes[orderId]?.get(rootId)
    }


}