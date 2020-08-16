package core.model.storage

import core.model.PollCreationTime
import core.model.SlackConversation
import core.model.base.PollID

class PollCreationTimesStorageImpl(): PollCreationTimesStorage {

    private val creationTimes = mutableMapOf<PollID, Map<SlackConversation, PollCreationTime>> ()

    override fun put(pollId: PollID, times: Map<SlackConversation, PollCreationTime>) {
        creationTimes[pollId] = times
    }

    override fun get(pollId: PollID): Map<SlackConversation, PollCreationTime>? {
        return creationTimes[pollId]
    }
}