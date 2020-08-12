package core.logic

class DataStorageTestVersion : DataStorage {

    private val customers = mutableMapOf<OrderId, UserId>()
    private val orders = mutableMapOf<OrderId, Order>()
    private val nodes = mutableMapOf<OrderId, MutableMap<UserId, Node>>()

    override fun addOrder(userId : UserId, order: Order) : DispatcherResponse {
        val orderId = order.id
        if (orders.containsKey(orderId)) {
            return DispatcherResponse(false, "this order already exist")
        }
        orders[orderId] = order
        nodes[orderId] = mutableMapOf(userId to Node(userId))
        customers[orderId] = userId
        return DispatcherResponse(true, "order added successfully")
    }

    override fun deleteOrder(orderId: OrderId) : DispatcherResponse {
        if (!orders.containsKey(orderId)) {
            return DispatcherResponse(false, "this order already exist")
        }
        orders.remove(orderId)
        nodes.remove(orderId)
        return DispatcherResponse(true, "order deleted successfully")
    }

    override fun getOrder(orderId: OrderId): Order? {
        return orders[orderId]
    }

    override fun addNode(orderId: OrderId, node: Node): DispatcherResponse {
        if (!nodes.containsKey(orderId)) {
            return DispatcherResponse(false, "this order doesn't exist")
        }
        if (nodes[orderId]?.get(node.getUserId()) != null) {
            return DispatcherResponse(false, "this node already exist")
        }
        nodes[orderId]?.set(node.getUserId(), node)
        return DispatcherResponse(true, "node added successfully")
    }

    override fun modifyNode(orderId: OrderId, node: Node): DispatcherResponse {
        if (!nodes.containsKey(orderId)) {
            return DispatcherResponse(false, "this order doesn't exist")
        }
        if (nodes[orderId]?.get(node.getUserId()) == null) {
            return DispatcherResponse(false, "this node doesn't exist")
        }
        nodes[orderId]?.set(node.getUserId(), node)
        return DispatcherResponse(true, "node modified successfully")
    }

    override fun getNode(orderId: OrderId, userId: UserId): Node? {
        return nodes[orderId]?.get(userId)
    }

    override fun deleteNode(orderId: OrderId, userId: UserId) : DispatcherResponse {
        if (nodes[orderId]?.get(userId) == null) {
            return DispatcherResponse(false, "this node doesn't exist")
        }
        nodes[orderId]?.remove(userId)
        return DispatcherResponse(true, "node deleted successfully")
    }

    override fun getCustomer(orderId : OrderId) : UserId? {
        return customers[orderId]
    }
}