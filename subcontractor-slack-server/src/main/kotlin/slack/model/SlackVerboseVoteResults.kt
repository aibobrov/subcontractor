package slack.model

import core.model.base.OptionID

data class SlackVerboseVoteResults(
    val results: Map<OptionID, List<SlackUserProfile>>
) : Map<OptionID, List<SlackUserProfile>> by results {
    val totalVoters: Int = results.values.fold(0) { acc, list -> acc + list.size }

    companion object {
        val EMPTY = SlackVerboseVoteResults(mapOf())
    }
}


data class SlackCompactVoteResults(
    val results: Map<OptionID, List<SlackUser>>
) : Map<OptionID, List<SlackUser>> by results {
    val totalVoters: Int = results.values.fold(0) { acc, list -> acc + list.size }
    companion object {
        val EMPTY = SlackCompactVoteResults(mapOf())
    }
}
