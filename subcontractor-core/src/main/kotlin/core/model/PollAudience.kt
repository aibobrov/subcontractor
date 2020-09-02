package core.model

import kotlinx.serialization.Serializable

@Serializable
data class PollAudience(
    val conversations: List<PollVoter>
) : List<PollVoter> by conversations {
    companion object {
        val EMPTY = PollAudience(listOf())
    }
}