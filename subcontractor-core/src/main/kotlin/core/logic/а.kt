package core.logic

import core.model.AgreeDisagreePoll
import core.model.PollAudience
import core.model.PollAuthor
import core.model.PollSerializer
import core.model.base.Poll
import core.model.base.PollTag
import core.model.base.VotingTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun main() {

    val p = AgreeDisagreePoll("id", "q", VotingTime.Unlimited, PollAuthor("", ""), true, true, true, PollAudience(listOf()), listOf() )

    val s = PollSerializer()
    val d = s.toJson(p)
    println(d)

    val agr = s.fromJson(d)
    println(agr)


}
