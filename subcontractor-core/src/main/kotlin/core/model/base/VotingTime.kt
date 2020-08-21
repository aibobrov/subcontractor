package core.model.base

import java.time.LocalDateTime

sealed class VotingTime {
    object Unlimited : VotingTime()

    class Ranged(val range: ClosedRange<LocalDateTime>) : VotingTime(), ScheduledTime {
        override val startDateTime: LocalDateTime get() = range.start
    }

    class From(override val startDateTime: LocalDateTime) : VotingTime(), ScheduledTime

    class UpTo(val date: LocalDateTime) : VotingTime()

    interface ScheduledTime {
        val startDateTime: LocalDateTime
    }
}
