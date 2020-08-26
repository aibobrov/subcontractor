package utils

import java.time.*

object DateUtils {
    // interval in minutes
    fun timesBy(interval: Long): List<LocalTime> {
        return listOf(LocalTime.MIN) + timesBy(interval, LocalTime.MIN)
    }

    // interval in minutes
    fun timesBy(interval: Long, fromTime: LocalTime): List<LocalTime> {
        val firstTime = round(fromTime, interval)
        val result = mutableListOf(firstTime)

        while (true) {
            val last = result.last()
            val nextTime = last.plusMinutes(interval)
            if (nextTime.isBefore(last))
                break
            result.add(nextTime)
        }

        return result.take(20)
    }

    fun round(time: LocalTime, byInterval: Long): LocalTime {
        if (time.minute % byInterval == 0.toLong()) {
            return time
        }
        val minutesDiff = byInterval - time.minute % byInterval

        return time.plusMinutes(minutesDiff).withSecond(0).withNano(0)
    }

    fun max(lhs: LocalTime, rhs: LocalTime): LocalTime {
        return if (lhs.isBefore(rhs)) rhs else lhs
    }

    fun max(lhs: LocalDate, rhs: LocalDate): LocalDate {
        return if (lhs.isBefore(rhs)) rhs else lhs
    }
}

val LocalDateTime.unixEpochTimestamp: Long
    // TODO: check timezone issue
    get() = atZone(ZoneId.systemDefault()).toEpochSecond()

val LocalTime.unixEpochTimestamp: Long
    get() = LocalDateTime.of(LocalDate.MIN, this).unixEpochTimestamp
