package slack.model

import core.model.PollType
import core.model.VoteResults
import core.model.base.OptionID
import core.model.base.Poll
import slack.service.SlackRequestProvider
import utils.unreachable

object SlackVoteResultsFactory {
    fun voteResults(
        poll: Poll,
        result: SlackCompactVoteResults,
        provider: SlackRequestProvider
    ): SlackPollVoteInfo {
        return when (poll.type) {
            PollType.SINGLE_CHOICE -> {
                val involvedUsers = result.values.flatMap { users -> users.map { it.id } }.toSet()
                val voteResultsFuture = provider
                    .userProfiles(involvedUsers)
                    .thenApply { profiles ->
                        SlackVerboseVoteResults(
                            result.mapValues { entry ->
                                entry.value.map { profiles[it.id] ?: unreachable() }
                            }
                        )
                    }
                SlackPollVoteInfo.Verbose(voteResultsFuture.get())
            }
            PollType.AGREE_DISAGREE -> SlackPollVoteInfo.Compact(result)
            PollType.ONE_TO_N -> SlackPollVoteInfo.Compact(result)
        }
    }

    fun emptyVoteResults(poll: Poll): SlackPollVoteInfo {
        return when(poll.type) {
            PollType.SINGLE_CHOICE -> SlackPollVoteInfo.Verbose(SlackVerboseVoteResults.EMPTY)
            PollType.AGREE_DISAGREE -> SlackPollVoteInfo.Compact(SlackCompactVoteResults.EMPTY)
            PollType.ONE_TO_N -> SlackPollVoteInfo.Compact(SlackCompactVoteResults.EMPTY)
        }
    }

    fun compactVoteResults(voteResults: VoteResults): SlackCompactVoteResults {
        val list : Map<OptionID, List<SlackUser>> = voteResults.results.mapValues { entry -> entry.value.map { voter -> SlackUser(voter.userID)}}
        return SlackCompactVoteResults(list)
    }
}
