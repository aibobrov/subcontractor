package slack.ui.components

import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import core.model.PollOption
import slack.model.SlackCompactVoteResults
import slack.model.SlackUser
import slack.ui.base.SlackBlockUIRepresentable
import utils.ProgressUtils
import java.text.DecimalFormat

class PollResultBlockView(
    private val options: List<PollOption>,
    private val results: SlackCompactVoteResults,
    private val isAnonymous: Boolean
) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        buildOptionsResult(builder, options, results)
        buildSummary(builder, results.totalVoters)
    }

    private fun buildOptionsResult(
        builder: LayoutBlockDsl,
        options: List<PollOption>,
        results: SlackCompactVoteResults
    ) {
        val totalVotes = results.totalVoters.toDouble()
        for (option in options) {
            val voters = results[option.id] ?: listOf()
            val ratio = voters.size / totalVotes
            buildOptionResult(builder, voters, option, ratio, voters.size)
        }
    }


    private fun buildOptionResult(
        builder: LayoutBlockDsl,
        users: List<SlackUser>,
        option: PollOption,
        ratio: Double,
        votesCount: Int
    ) {
        val addition = if (!isAnonymous) "\n${userListString(users)}" else ""
        val contentString = markdownProgressString(option, ratio, votesCount) + addition
        builder.section {
            markdownText(contentString)
        }
    }

    private fun buildSummary(builder: LayoutBlockDsl, totalVotes: Int) {
        builder.context {
            elements {
                plainText(buildTotalVotes(totalVotes))
            }
        }
    }

    companion object {
        fun markdownProgressString(option: PollOption, ratio: Double, votesCount: Int): String {
            val progress = ProgressUtils.progressString(ratio)

            val percentage = ratio * 100
            return "*_${option.content}_*\n$progress  ${DECIMAL_FORMATTER.format(percentage)}% ($votesCount)"
        }

        fun userListString(users: List<SlackUser>): String {
            return users.joinToString(" ") { "<@${it.id}>" }
        }


        fun buildTotalVotes(votesCount: Int): String {
            return "Total votes: $votesCount"
        }

        val DECIMAL_FORMATTER = DecimalFormat("#.##")
    }
}
