package core.model.storage

import core.logic.DataStorage
import core.model.base.Poll
import core.model.base.PollID
import core.model.base.UserID

class LiquidPollRepositoryImpl(private val storage : DataStorage) : LiquidPollRepository {

    override fun put(pollID: PollID, userID: UserID, poll: Poll) {
        storage.addOrder(pollID, userID, poll)
    }
    override fun get(pollID: PollID): Poll? {
        return storage.getOrder(pollID)
    }
}
