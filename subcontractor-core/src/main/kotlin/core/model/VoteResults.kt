package core.model

import core.model.base.OptionID


data class VoteResults(val results: MutableMap<OptionID, MutableList<Voter>>) : Map<OptionID, List<Voter>> by results {
    val totalVoters: Int = results.values.fold(0) { acc, list -> acc + list.size }
}
