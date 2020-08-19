package core.model

data class PollAudience(
    val conversations: List<PollVoter>
) : List<PollVoter> by conversations {
    companion object {
        val EMPTY = PollAudience(listOf())
    }
}