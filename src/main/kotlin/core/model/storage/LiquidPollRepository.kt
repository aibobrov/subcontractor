package core.model.storage

import core.model.base.Poll

interface LiquidPollRepository {
    fun put(viewId: String, poll: Poll)
    fun get(viewId: String): Poll?
}
