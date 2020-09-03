package core.model

import core.logic.Serializer
import core.model.base.Poll
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.*

class PollSerializer : Serializer<Poll> {

    private val module = SerializersModule {
        polymorphic(Poll::class) {
            subclass(AgreeDisagreePoll::class)
            subclass(SingleChoicePoll::class)
            subclass(OneToNPoll::class)
        }
    }

    override fun toJson(obj: Poll): String {
        val format = Json { serializersModule = module }
        return format.encodeToString(obj)
    }

    override fun fromJson(string: String): Poll {
        val format = Json { serializersModule = module }
        return format.decodeFromString(string)
    }
}