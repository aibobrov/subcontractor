package core.model.storage

import core.model.PollCreationTime
import core.model.PollVoter
import core.model.base.PollID
import java.util.concurrent.ConcurrentHashMap

class PollCreationTimesStorageImpl : PollCreationTimesStorage {
    private val creationTimes: MutableMap<PollID, MutableMap<PollVoter, PollCreationTime>> = ConcurrentHashMap()

    override fun put(pollId: PollID, times: MutableMap<PollVoter, PollCreationTime>) {
        creationTimes[pollId] = times
    }

    override fun get(pollId: PollID): MutableMap<PollVoter, PollCreationTime> {
        return creationTimes[pollId] ?: mutableMapOf()
    }
}