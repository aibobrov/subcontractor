package core.model.storage

import core.model.PollCreationTime
import core.model.SlackConversation
import core.model.base.PollID

interface PollCreationTimesStorage {
    fun put(pollId : PollID, times : Map<SlackConversation, PollCreationTime>)
    fun get(pollId: PollID) : Map<SlackConversation, PollCreationTime>?
}