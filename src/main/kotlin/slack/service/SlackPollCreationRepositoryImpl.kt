package slack.service

import slack.model.PollBuilder

// in-memory storage
class SlackPollCreationRepositoryImpl : SlackPollCreationRepository {
    private val storage: MutableMap<String, PollBuilder> = mutableMapOf()

    override fun put(viewId: String, pollBuilder: PollBuilder) {
        storage[viewId] = pollBuilder
    }

    override fun get(viewId: String): PollBuilder? {
        return storage[viewId]
    }

    override fun remove(viewId: String) {
        storage.remove(viewId)
    }
}
