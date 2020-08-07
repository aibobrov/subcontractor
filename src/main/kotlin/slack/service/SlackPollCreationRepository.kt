package slack.service

import core.model.PollBuilder

interface SlackPollCreationRepository {
    fun put(viewId: String, pollBuilder: PollBuilder)

    fun get(viewId: String): PollBuilder?

    fun remove(viewId: String)
}
