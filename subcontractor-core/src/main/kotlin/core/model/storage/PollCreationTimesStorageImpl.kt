package core.model.storage

import core.model.PollCreationTime
import core.model.PollVoter
import core.model.base.PollID

class PollCreationTimesStorageImpl(): PollCreationTimesStorage {

    private val creationTimes = mutableMapOf<PollID, Map<PollVoter, PollCreationTime>> ()

    override fun put(pollId: PollID, times: Map<PollVoter, PollCreationTime>) {
        creationTimes[pollId] = times
    }

    override fun get(pollId: PollID): Map<PollVoter, PollCreationTime>? {
        return creationTimes[pollId]
    }
}