package slack.service

import core.model.base.Poll
import core.model.base.PollID

class SlackPollCreationRepositoryImpl : SlackPollCreationRepository {
    private val storage: MutableMap<PollID, Poll> = mutableMapOf()

    override fun put(pollID: PollID, poll: Poll) {
        storage[pollID] = poll
    }

    override fun get(pollID: PollID): Poll? = storage[pollID]
}
