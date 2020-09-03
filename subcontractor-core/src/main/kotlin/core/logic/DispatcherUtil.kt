package core.logic

class DispatcherUtil {
    companion object {
        fun getCurrentTime(): DispatcherTime {
            val time = System.currentTimeMillis()
            return DispatcherTime(time)
        }
    }
}