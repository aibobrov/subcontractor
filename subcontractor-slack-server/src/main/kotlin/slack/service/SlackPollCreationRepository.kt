package slack.service

import slack.model.SlackPollBuilder

interface SlackPollCreationRepository {
    fun put(viewId: String, builder: SlackPollBuilder)

    fun get(viewId: String): SlackPollBuilder?

    fun remove(viewId: String)
}
