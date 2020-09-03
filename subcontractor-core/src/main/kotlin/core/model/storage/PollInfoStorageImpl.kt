package core.model.storage

import core.model.PollCreationTime
import core.model.PollVoter
import core.model.base.PollID

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