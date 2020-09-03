package core.model.storage

import core.logic.OrderId
import core.model.PollCreationTime
import core.model.PollVoter
import core.model.base.Poll
import core.model.base.PollID
import core.model.base.UserID

interface PollInfoStorage {
    fun putPollCreationTimes(pollId: PollID, times: MutableMap<PollVoter, PollCreationTime>)
    fun getPollCreationTimes(pollId: PollID): MutableMap<PollVoter, PollCreationTime>
}