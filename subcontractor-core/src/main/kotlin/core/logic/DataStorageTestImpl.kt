package core.logic

class DataStorageTestVersion : DataStorage {

    private val customers = mutableMapOf<OrderId, UserId>()
    private val orders = mutableMapOf<OrderId, Order>()
    private val nodes = mutableMapOf<OrderId, MutableMap<UserId, Node>>()

    override fun addOrder(orderId: OrderId, userId : UserId, order: Order) : DispatcherError {
        if (orders.containsKey(orderId)) {
            return DispatcherError.ORDER_ALREADY_EXIST
        }
        orders[orderId] = order
        nodes[orderId] = mutableMapOf(userId to Node(userId))
        customers[orderId] = userId
        return DispatcherError.EMPTY_ERROR
    }

    override fun deleteOrder(orderId: OrderId) : DispatcherError {
        if (!orders.containsKey(orderId)) {
            return DispatcherError.ORDER_ALREADY_EXIST
        }
        orders.remove(orderId)
        nodes.remove(orderId)
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

    override fun deleteNode(orderId: OrderId, userId: UserId) : DispatcherError {
        if (nodes[orderId]?.get(userId) == null) {
            return DispatcherError.NODE_DOES_NOT_EXIST
        }
        nodes[orderId]?.remove(userId)
        return DispatcherError.EMPTY_ERROR
    }

    override fun getCustomer(orderId : OrderId) : UserId? {
        return customers[orderId]
    }
}