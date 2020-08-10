package utils

import slack.server.base.SlackConstant

object ProgressUtils {
    private const val PROGRESS_SYMBOL = "█"

    private const val SPACE_SYMBOL = "\u2062"
    private const val FULL_PROGRESS_LENGTH = SlackConstant.FULL_PROGRESS_LENGTH

    // ratio \in 0..1.0
    fun progressString(ratio: Double): String {
        val progressTimes = (ratio * FULL_PROGRESS_LENGTH).toInt()
        val spaceTimes = ((1 - ratio) * FULL_PROGRESS_LENGTH).toInt() + 1
        val spaceString = SPACE_SYMBOL.repeat(spaceTimes ).toList().joinToString(" ")
        val progressString = PROGRESS_SYMBOL.repeat(progressTimes)
        return "`$progressString$spaceString`"
    }
}
