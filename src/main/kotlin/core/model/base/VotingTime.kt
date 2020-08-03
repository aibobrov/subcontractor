package core.model.base

import java.time.LocalDateTime

sealed class VotingTime {
    object Unlimited : VotingTime()

    class Ranged(val range: ClosedRange<LocalDateTime>) : VotingTime()

    class UpTo(val date: LocalDateTime) : VotingTime()
}
