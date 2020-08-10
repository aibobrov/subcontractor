package slack.model

sealed class SlackPollVoteInfo {
    class Compact(val info: SlackCompactVoteResults) : SlackPollVoteInfo() {
        override fun compact(): SlackCompactVoteResults = info
    }

    class Verbose(val info: SlackVerboseVoteResults) : SlackPollVoteInfo() {
        override fun compact(): SlackCompactVoteResults = SlackCompactVoteResults(
            info.mapValues { entry ->
                entry.value.map { SlackUser(it.id) }
            }
        )
    }

    abstract fun compact(): SlackCompactVoteResults
}
