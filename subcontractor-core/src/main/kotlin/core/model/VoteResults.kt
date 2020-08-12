package core.model

import core.model.base.OptionID

data class VoteResults(val results: MutableMap<OptionID, Voter>) : Map<OptionID, Set<Voter>> by results {
    val totalVoters: Int = results.values.fold(0) { acc, list -> acc + list.size }

    companion object {
        val EMPTY = VoteResults(mapOf())
        fun empty(options: List<PollOption>): VoteResults {
            return VoteResults(
                options.map { it.id to setOf<Voter>() }.toMap()
            )
        }
    }
}
