package core.logic

import kotlinx.serialization.Serializable

@Serializable
class Worker(val userId: UserId) {

    private val orders = mutableListOf<OrderId>()
    private val delegations = mutableMapOf<OrderId, DispatcherTime>()

    fun addOrder(orderId: OrderId) {
        orders.add(orderId)
    }

    fun containsDelegation(orderId: OrderId, time: DispatcherTime): Boolean {
        return delegations[orderId] == time
    }

    fun addDelegation(orderId: OrderId, time: DispatcherTime) {
        delegations[orderId] = time
    }

    fun deleteDelegation(orderId: OrderId) {
        delegations.remove(orderId)
    }

    fun getOrders(): List<OrderId> {
        return orders
    }

}