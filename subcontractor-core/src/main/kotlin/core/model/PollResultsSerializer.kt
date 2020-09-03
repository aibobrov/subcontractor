package core.model

import core.logic.Serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PollResultsSerializer : Serializer<PollResults> {
    override fun toJson(obj: PollResults): String {
        return Json.encodeToString(obj)
    }

    override fun fromJson(string: String): PollResults {
        return Json.decodeFromString(string)
    }
}