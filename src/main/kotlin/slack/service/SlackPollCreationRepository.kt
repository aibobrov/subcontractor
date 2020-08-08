package slack.service

import slack.model.PollBuilder

interface SlackPollCreationRepository {
    fun put(viewId: String, pollBuilder: PollBuilder)

    fun get(viewId: String): PollBuilder?

    fun remove(viewId: String)
}
