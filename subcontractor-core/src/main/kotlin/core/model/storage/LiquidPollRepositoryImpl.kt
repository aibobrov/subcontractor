package core.model.storage

import core.model.base.Poll
import core.model.base.PollID

class LiquidPollRepositoryImpl : LiquidPollRepository {
    private val storage: MutableMap<PollID, Poll> = mutableMapOf()

    override fun put(pollID: PollID, poll: Poll) {
        storage[pollID] = poll
    }
    override fun get(pollID: PollID): Poll? {
        return storage[pollID]
    }
}
