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

    override fun putPollCreationTimes(pollId: PollID, times: MutableMap<PollVoter, PollCreationTime>) {
        creationTimes[pollId] = times
    }

    override fun getPollCreationTimes(pollId: PollID): MutableMap<PollVoter, PollCreationTime> {
       creationTimes[pollId]?.let {
           return it
       }.run {
           return mutableMapOf()
       }
    }

}