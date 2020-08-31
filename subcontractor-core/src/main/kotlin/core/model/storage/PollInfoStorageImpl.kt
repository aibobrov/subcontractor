package core.model.storage

import core.logic.DispatcherTime
import core.logic.OrderId
import core.model.PollCreationTime
import core.model.PollVoter
import core.model.base.Poll
import core.model.base.PollID
import core.model.base.UserID

class PollInfoStorageImpl : PollInfoStorage {

    private val creationTimes = mutableMapOf<PollID, MutableMap<PollVoter, PollCreationTime>>()
    private val orders = mutableMapOf<PollID, MutableMap<UserID, MutableMap<PollCreationTime, OrderId>>>()
    private val polls = mutableMapOf<PollID, Poll>()

    override fun put(pollId: PollID, times: MutableMap<PollVoter, PollCreationTime>) {
        creationTimes[pollId] = times
    }

    override fun get(pollId: PollID): MutableMap<PollVoter, PollCreationTime> {
       creationTimes[pollId]?.let {
           return it
       }.run {
           return mutableMapOf()
       }
    }

    override fun putPoll(pollId: PollID, poll: Poll) {
        polls[pollId] = poll
    }

    override fun getPoll(pollId: PollID): Poll? {
        return polls[pollId]
    }

    override fun putPollOrder(pollId: PollID, orderId: OrderId, time: PollCreationTime){
       if (orders[pollId] == null) {
           orders[pollId] = mutableMapOf()
       }
        if (orders[pollId]!![orderId.executorId] == null) {
            orders[pollId]!![orderId.executorId] = mutableMapOf()
        }
        orders[pollId]!![orderId.executorId]!![time] = orderId
    }

    override fun getPollOrder(pollId: PollID, userId: UserID, time: PollCreationTime): OrderId? {
        return orders[pollId]?.get(userId)?.get(time)
    }
}