package slack.server.base

import com.google.gson.Gson
import com.slack.api.util.json.GsonFactory

object SlackConstant {
    val GSON: Gson = GsonFactory.createSnakeCase()

    const val TIME_INTERVAL: Long = 15 // in minutes
}
