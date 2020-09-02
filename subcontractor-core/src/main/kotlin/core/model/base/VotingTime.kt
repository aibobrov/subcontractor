package core.model.base

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
sealed class VotingTime {
    @Serializable
    object Unlimited : VotingTime()

    @Serializable
    class Ranged(val range: ClosedRange<@Contextual LocalDateTime>) : VotingTime() {
        val startDateTime: LocalDateTime get() = range.start
    }

    @Serializable
    class From(val startDateTime: @Contextual LocalDateTime) : VotingTime()

    @Serializable
    class UpTo(val date: @Contextual LocalDateTime) : VotingTime()


}
