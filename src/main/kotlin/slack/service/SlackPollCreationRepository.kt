package slack.service

import core.model.base.Poll
import core.model.base.PollID

interface SlackPollCreationRepository {
    fun put(pollID: PollID, poll: Poll)

    fun get(pollID: PollID): Poll?
}
