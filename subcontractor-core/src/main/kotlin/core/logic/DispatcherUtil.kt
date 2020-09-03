package core.logic

object DispatcherUtil {
    fun getCurrentTime(): DispatcherTime {
        val time = System.currentTimeMillis()
        return DispatcherTime(time)
    }
}