package core.model

import core.model.base.OptionID

data class VoteResults(val results: Map<OptionID, List<Voter>>) : Map<OptionID, List<Voter>> by results {
    val totalVoters: Int = results.values.fold(0) { acc, list -> acc + list.size }
}
