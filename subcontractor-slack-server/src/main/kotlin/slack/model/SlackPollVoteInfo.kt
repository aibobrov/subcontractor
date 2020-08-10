package slack.model

import core.model.VoteResults
import core.model.VoteWork
import core.model.Voter

sealed class SlackPollVoteInfo {
    class Compact(val info: VoteResults) : SlackPollVoteInfo() {
        override fun voteResults(): VoteResults = info
    }

    class Verbose(val info: SlackVoteResults) : SlackPollVoteInfo() {
        override fun voteResults(): VoteResults = VoteResults(
            info.mapValues { entry ->
                entry.value.map { Voter(it.id, VoteWork.Vote(entry.key)) }
            }
        )
    }

    abstract fun voteResults(): VoteResults
}
