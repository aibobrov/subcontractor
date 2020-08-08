package slack.server.base

import com.google.gson.Gson
import com.slack.api.util.json.GsonFactory

object Constant {
    val GSON: Gson = GsonFactory.createSnakeCase()
}
