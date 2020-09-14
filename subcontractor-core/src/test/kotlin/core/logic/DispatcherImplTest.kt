
package core.logic

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random

internal class DispatcherImplTest {

    private lateinit var dispatcher : Dispatcher<Int>

    @BeforeEach
    fun setUp() {
        dispatcher = DispatcherImpl(DataStorageTestImpl())
    }

    @Test
    fun registerWork() {
        val worksId = mutableSetOf<Int>()
        for (i in 0..10000) {
            val randomId = Random.nextInt(100)
            val userId =  Random.nextInt(10)
            try {
                dispatcher.registerWork(randomId.toString(), userId.toString(), listOf())
                assertEquals(false, worksId.contains(randomId))
                worksId.add(randomId)
            } catch (e: DispatcherError.WorkAlreadyExists) {
                assertEquals(true, worksId.contains(randomId))
            }
        }
    }

    @Test
    fun deleteWork() {
        val worksId = mutableSetOf<Int>()
        for (i in 0..20000) {
            var randomId = Random.nextInt(100)
            val userId =  Random.nextInt(10)
            try {
                dispatcher.registerWork(randomId.toString(), userId.toString(), listOf())
                assertEquals(false, worksId.contains(randomId))
                worksId.add(randomId)
            } catch (e: DispatcherError.WorkAlreadyExists) {
                assertEquals(true, worksId.contains(randomId))
            }
            randomId = Random.nextInt(100)
            try {
                dispatcher.deleteWork(randomId.toString())
                assertEquals(true, worksId.contains(randomId))
                worksId.remove(randomId)
            } catch (e: DispatcherError.WorkNotFound) {
                assertEquals(false, worksId.contains(randomId))
            }
        }
    }

    @Test
    fun delegateOrder() {
        val workId = "1"
        val userId = "1"
        val toUsersId = listOf("1", "2", "3")
        val ordersId = mutableMapOf<UserId, OrderId>()
        ordersId.putAll(dispatcher.registerWork(workId, userId, toUsersId))
        try {
            val orderId = dispatcher.delegateOrder(workId, ordersId["1"] ?: error("order id is not found"), "1", "4")
            ordersId[orderId.executorId] = orderId
        } catch (e: DispatcherError) {
            fail(e.message)
        }
        try {
            val orderId = dispatcher.delegateOrder(workId, ordersId["2"] ?: error("order id is not found"), "2", "5")
            ordersId[orderId.executorId] = orderId
        } catch (e: DispatcherError) {
            fail(e.message)
        }
        try {
            val orderId = dispatcher.delegateOrder(workId, ordersId["4"] ?: error("order id is not found"), "4", "2")
            ordersId[orderId.executorId] = orderId
        } catch (e: DispatcherError) {
            fail(e.message)
        }
        try {
            val orderId = dispatcher.delegateOrder(workId, ordersId["5"] ?: error("order id is not found"), "5", "2")
            ordersId[orderId.executorId] = orderId
            fail()
        } catch (e: DispatcherError) { }

        try {
            val orderId = dispatcher.delegateOrder(workId, ordersId["5"] ?: error("order id is not found"), "5", "3")
            ordersId[orderId.executorId] = orderId
        } catch (e: DispatcherError) {
            fail(e.message)
        }
    }

    @Test
    fun delegateAllOrders() {
        val workId = "1"
        val userId = "1"
        val toUsersId = listOf("1", "2", "3", "4")
        val ordersId = mutableMapOf<UserId, OrderId>()
        ordersId.putAll(dispatcher.registerWork(workId, userId, toUsersId))
        try {
            dispatcher.delegateAllOrders(workId, "1", "5")
        } catch (e: DispatcherError) {
            fail(e.message)
        }
        try {
            dispatcher.delegateAllOrders(workId, "2", "5")
        } catch (e: DispatcherError) {
            fail(e.message)
        }
        try {
            dispatcher.delegateAllOrders(workId, "3", "5")
        } catch (e: DispatcherError) {
            fail(e.message)
        }
        try {
            dispatcher.delegateAllOrders(workId, "5", "6")
        } catch (e: DispatcherError) {
            fail(e.message)
        }
        try {
            dispatcher.delegateAllOrders(workId, "6", "1")
            fail()
        } catch (e: DispatcherError) { }
        try {
            dispatcher.delegateAllOrders(workId, "6", "2")
            fail()
        } catch (e: DispatcherError) { }
        try {
            dispatcher.delegateAllOrders(workId, "6", "3")
            fail()
        } catch (e: DispatcherError) { }

        try {
            dispatcher.delegateAllOrders(workId, "6", "4")
        } catch (e: DispatcherError) {
            fail(e.message)
        }
    }

    @Test
    fun executeOrder() {
        TODO()
    }

    @Test
    fun executeAllOrders() {
        TODO()
    }

    @Test
    fun cancelExecution() {
        TODO()
    }

    @Test
    fun cancelAllExecutions() {
        TODO()
    }

    @Test
    fun cancelDelegation() {
        TODO()

    }

    @Test
    fun cancelAllDelegations() {
        TODO()
    }

    @Test
    fun getWorkResults() {
        TODO()
    }

    @Test
    fun getTheMostActiveRealExecutors() {
        TODO()
    }

    @Test
    fun getCustomerId() {
        val ids = mutableMapOf<String, String>()
        for (i in 0..10000) {
            try {
                val workId = Random.nextInt(1000).toString()
                val userId =  Random.nextInt(1000).toString()
                dispatcher.registerWork(workId, userId, listOf())
                ids[workId] = userId
            } catch (e: DispatcherError) { }
        }
        for (i in 0..10000) {
            try {
                val workId = Random.nextInt(1000).toString()
                val id = dispatcher.getCustomerId(workId)
                assertEquals(ids[workId], id)
            } catch (e: DispatcherError) { }
        }
    }

    @Test
    fun getExecutorsId() {
        val usersList1 = listOf("1", "2", "3")
        val usersList2 = listOf("aaaa", "b", "cc")
        val usersList3 = listOf("vrfe", "vfrced", "3rcf", "qwd", "vrfce")
        dispatcher.registerWork("1", "1", usersList1)
        dispatcher.registerWork("2", "2", usersList2)
        dispatcher.registerWork("3", "3", usersList3)
        assertEquals(usersList1, dispatcher.getExecutorsId("1"))
        assertEquals(usersList2, dispatcher.getExecutorsId("2"))
        assertEquals(usersList3, dispatcher.getExecutorsId("3"))
    }

}
