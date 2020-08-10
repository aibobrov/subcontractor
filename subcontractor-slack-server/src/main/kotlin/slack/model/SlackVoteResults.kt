package slack.model

import core.model.base.OptionID

data class SlackVoteResults(
    val results: Map<OptionID, List<SlackUserProfile>>
) : Map<OptionID, List<SlackUserProfile>> by results {
    val totalVoters: Int = results.values.fold(0) { acc, list -> acc + list.size }
}
