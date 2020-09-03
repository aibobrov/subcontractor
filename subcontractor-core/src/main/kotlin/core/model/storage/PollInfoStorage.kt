package core.model.storage

import core.model.PollCreationTime
import core.model.PollVoter
import core.model.base.PollID

interface PollInfoStorage {
    fun putPollCreationTimes(pollId: PollID, times: MutableMap<PollVoter, PollCreationTime>)
    fun getPollCreationTimes(pollId: PollID): MutableMap<PollVoter, PollCreationTime>
}