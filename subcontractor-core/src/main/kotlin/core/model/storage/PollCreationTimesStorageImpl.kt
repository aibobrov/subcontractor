package core.model.storage

import core.model.PollCreationTime
import core.model.PollVoter
import core.model.base.PollID

class PollCreationTimesStorageImpl : PollCreationTimesStorage {

    private val creationTimes = mutableMapOf<PollID, MutableMap<PollVoter, PollCreationTime>>()

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
}