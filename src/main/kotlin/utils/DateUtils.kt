package utils

import java.time.LocalTime

object DateUtils {
    // interval in minutes
    fun timesBy(interval: Long): List<LocalTime> {
        return listOf(LocalTime.MIN) + timesBy(interval, LocalTime.MIN)
    }

    // interval in minutes
    fun timesBy(interval: Long, fromTime: LocalTime): List<LocalTime> {
        val minutesDiff = interval - fromTime.minute % interval

        val firstTime = fromTime.plusMinutes(minutesDiff)
        val result = mutableListOf(firstTime)

        while (true) {
            val last = result.last()
            val nextTime = last.plusMinutes(interval)
            if (nextTime.isBefore(last))
                break
            result.add(nextTime)
        }

        return result
    }
}
