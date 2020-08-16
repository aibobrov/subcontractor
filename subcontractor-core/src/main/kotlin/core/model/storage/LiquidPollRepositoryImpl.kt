package core.model.storage

import core.logic.DataStorage
import core.model.PollCreationTime
import core.model.SlackConversation
import core.model.base.Poll
import core.model.base.PollID
import core.model.base.UserID

class LiquidPollRepositoryImpl(private val storage : DataStorage,
                               private val timesStorage: PollCreationTimesStorage) : LiquidPollRepository {

    override fun put(pollID: PollID, userID: UserID, poll: Poll) {
        storage.addOrder(pollID, userID, poll)
    }

    override fun get(pollID: PollID): Poll? {
        return storage.getOrder(pollID)
    }

    override fun putPollTime(pollID: PollID, times: Map<SlackConversation, PollCreationTime>) {
        timesStorage.put(pollID, times)
    }

    override fun getPollTime(pollID: PollID): Map<SlackConversation, PollCreationTime>? {
        return timesStorage.get(pollID)
    }
}
