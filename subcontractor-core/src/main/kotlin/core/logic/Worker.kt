package core.logic

import kotlinx.serialization.Serializable

@Serializable
class Worker(val userId: UserId) {

    private val orders = mutableListOf<OrderId>()
    private val delegations = mutableMapOf<UserId, Pair<UserId, Pair<UserId, DispatcherTime>>>()

    fun addOrder(orderId: OrderId) {
        orders.add(orderId)
    }

    fun containsDelegation(orderId: OrderId, time: DispatcherTime): Boolean {
        return delegations[orderId.customerId]?.second?.second == time
    }

    fun addDelegation(orderId: OrderId, toUserId: UserId, time: DispatcherTime) {
        if (!orders.contains(orderId)) {
            return
        }
        delegations[orderId.customerId] = Pair(orderId.executorId, Pair(toUserId, time))
    }

    fun deleteDelegation(orderId: OrderId) {
        delegations.remove(orderId.customerId)
    }

    fun getOrders(): List<OrderId> {
        return orders
    }

    fun getDelegations(): MutableMap<UserId, Pair<UserId, Pair<UserId, DispatcherTime>>> {
        return delegations
    }

}