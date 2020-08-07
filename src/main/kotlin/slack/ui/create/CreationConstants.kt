package slack.ui.create

import com.google.gson.Gson
import com.slack.api.util.json.GsonFactory

object CreationConstants {
    object BlockID {
        const val AUDIENCE = "AUDIENCE_BLOCK_ID"
        const val QUESTION = "QUESTION_BLOCK_ID"
    }

    object CallbackID {
        const val ADD_OPTION_VIEW_SUBMISSION = "ADD_NEW_OPTION_BUTTON"
        const val CREATION_VIEW_SUBMISSION = "CREATION_VIEW_SUBMISSION"
    }

    object ActionID {
        const val OPTION_ACTION_OVERFLOW = "OPTION_ACTION_OVERFLOW"
        const val SINGLE_POLL_EDIT_CHOICE = "SINGLE_POLL_ADD_CHOICE"
        const val POLL_TYPE = "POLL_TYPE"
        const val POLL_AUDIENCE = "POLL_AUDIENCE"
        const val POLL_QUESTION = "POLL_QUESTION"
        const val ADD_NEW_OPTION_BUTTON = "ADD_NEW_OPTION_BUTTON"
    }

    val GSON: Gson = GsonFactory.createSnakeCase()
}
