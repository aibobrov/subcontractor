package core.model.base

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
sealed class VotingTime {
    @Serializable
    object Unlimited : VotingTime()

    @Serializable
    class Ranged(val range: ClosedRange<@Contextual LocalDateTime>) : VotingTime(), ScheduledTime {
        override val startDateTime: LocalDateTime get() = range.start
    }

    @Serializable
    class From(override val startDateTime: @Contextual LocalDateTime) : VotingTime(), ScheduledTime

    @Serializable
    class UpTo(val date: @Contextual LocalDateTime) : VotingTime()

    interface ScheduledTime {
        val startDateTime: LocalDateTime
    }
}
