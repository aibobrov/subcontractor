package core.model.storage

import core.logic.OrderId
import core.model.PollCreationTime
import core.model.PollVoter
import core.model.base.Poll
import core.model.base.PollID
import core.model.base.UserID

interface PollInfoStorage {
    fun putPoll(pollId: PollID, poll: Poll)
    fun getPoll(pollId: PollID): Poll?
    fun putPollOrder(pollId: PollID, orderId: OrderId, time: PollCreationTime)
    fun getPollOrder(pollId: PollID, userId: UserID, time: PollCreationTime): OrderId?
    fun put(pollId: PollID, times: MutableMap<PollVoter, PollCreationTime>)
    fun get(pollId: PollID): MutableMap<PollVoter, PollCreationTime>
}