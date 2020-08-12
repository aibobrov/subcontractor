package slack.server.base

import com.google.gson.Gson
import com.slack.api.util.json.GsonFactory

object SlackConstant {
    val GSON: Gson = GsonFactory.createSnakeCase()

    const val TIME_INTERVAL: Long = 1 // in minutes
    const val FULL_PROGRESS_LENGTH = 35
}
