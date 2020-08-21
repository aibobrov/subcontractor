package core.model.storage

import core.model.PollCreationTime
import core.model.PollVoter
import core.model.base.PollID

interface PollCreationTimesStorage {
    fun put(pollId: PollID, times: Map<PollVoter, PollCreationTime>)
    fun get(pollId: PollID): Map<PollVoter, PollCreationTime>?
}