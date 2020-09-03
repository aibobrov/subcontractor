package core.logic

import kotlinx.serialization.Serializable

@Serializable
class Worker(val userId: UserId) {

    private val orders = mutableListOf<OrderId>()
    private val delegations = mutableMapOf<UserId, MutableMap<UserId, DispatcherTime>>()

    fun addOrder(orderId: OrderId) {
        orders.add(orderId)
    }

    fun containsDelegation(orderId: OrderId, time: DispatcherTime): Boolean {
        return delegations[orderId.customerId]?.get(orderId.executorId) == time
    }

    fun addDelegation(orderId: OrderId, time: DispatcherTime) {
        if (delegations[orderId.customerId] == null) {
            delegations[orderId.customerId] = mutableMapOf()
        }
        delegations[orderId.customerId]!![orderId.executorId] = time
    }

    fun deleteDelegation(orderId: OrderId) {
        delegations[orderId.customerId]?.remove(orderId.executorId)
    }

    fun getOrders(): List<OrderId> {
        return orders
    }

}