package core.model.storage

import core.model.base.Poll
import core.model.base.PollID

interface LiquidPollRepository {
    fun put(pollID: PollID, poll: Poll)
    fun get(pollID: PollID): Poll?
}
