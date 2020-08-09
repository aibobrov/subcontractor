package slack.service

import slack.model.SlackPollBuilder

// in-memory storage
class SlackPollCreationRepositoryImpl : SlackPollCreationRepository {
    private val storage: MutableMap<String, SlackPollBuilder> = mutableMapOf()

    override fun put(viewId: String, builder: SlackPollBuilder) {
        storage[viewId] = builder
    }

    override fun get(viewId: String): SlackPollBuilder? {
        return storage[viewId]
    }

    override fun remove(viewId: String) {
        storage.remove(viewId)
    }
}
