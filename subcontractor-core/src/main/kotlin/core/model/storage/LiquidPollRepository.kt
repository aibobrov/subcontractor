package core.model.storage

import core.model.PollCreationTime
import core.model.SlackConversation
import core.model.base.Poll
import core.model.base.PollID
import core.model.base.UserID

interface LiquidPollRepository {
    fun put(pollID: PollID, userID: UserID, poll: Poll)
    fun get(pollID: PollID): Poll?
    fun putPollTime(pollID: PollID, times:  Map<SlackConversation, PollCreationTime>)
    fun getPollTime(pollID: PollID):  Map<SlackConversation, PollCreationTime>?
}
