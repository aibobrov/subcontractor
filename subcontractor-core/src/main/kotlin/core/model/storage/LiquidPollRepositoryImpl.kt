package core.model.storage

import core.model.base.Poll

class LiquidPollRepositoryImpl : LiquidPollRepository {
    private val storage: MutableMap<String, Poll> = mutableMapOf()

    override fun put(viewId: String, poll: Poll) {
        storage[viewId] = poll
    }
    override fun get(viewId: String): Poll? {
        return storage[viewId]
    }
}
