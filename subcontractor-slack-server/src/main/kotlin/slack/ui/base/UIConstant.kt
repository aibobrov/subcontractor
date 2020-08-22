package slack.ui.base

import com.google.gson.Gson
import com.slack.api.util.json.GsonFactory
import core.model.base.OptionID
import core.model.base.Poll
import core.model.base.PollID
import core.model.base.UserID
import java.time.format.DateTimeFormatter

object UIConstant {
    object BlockID {
        const val QUESTION = "QUESTION_BLOCK_ID"
    }

    object CallbackID {
        const val ADD_OPTION_VIEW_SUBMISSION = "ADD_NEW_OPTION_BUTTON"
        const val CREATION_VIEW_SUBMISSION = "CREATION_VIEW_SUBMISSION"
    }

    object ActionID {
        val EMPTY = "EMPTY_.*".toPattern()
        fun emptyAction(id: String): String = "EMPTY_$id"

        // View interactivity
        const val OPTION_ACTION_OVERFLOW = "OPTION_ACTION_OVERFLOW"
        const val SINGLE_POLL_EDIT_CHOICE = "SINGLE_POLL_ADD_CHOICE"
        const val POLL_TYPE = "POLL_TYPE"
        const val POLL_NUMBER = "POLL_NUMBER"
        const val POLL_AUDIENCE = "POLL_AUDIENCE"
        const val POLL_QUESTION = "POLL_QUESTION"
        const val ADD_NEW_OPTION_BUTTON = "ADD_NEW_OPTION_BUTTON"
        const val START_DATE_PICKER = "START_DATE_PICKER"
        const val FINISH_DATE_PICKER = "FINISH_DATE_PICKER"
        const val START_TIME_PICKER = "START_TIME_PICKER"

        // Advanced setting interactivity
        const val FINISH_TIME_PICKER = "FINISH_TIME_PICKER"
        const val ANONYMOUS_TOGGLE = "ANONYMOUS_CHECKBOX"
        const val SHOW_RESPONSES_TOGGLE = "SHOW_RESPONSES_CHECKBOX"
        const val START_DATETIME_TOGGLE = "START_TIME_ENABLE"
        const val FINISH_DATETIME_TOGGLE = "FINISH_TIME_ENABLE"

        // Message interactivity
        const val DELEGATE_VOTE = "DELEGATE_VOTE"
        const val CANCEL_VOTE = "CANCEL_VOTE"
        const val CANCEL_DELEGATION = "CANCEL_DELEGATION"
        val VOTE = "VOTE#(.*)#(.*)".toPattern()
        fun voteAction(pollID: PollID, optionID: OptionID): String = "VOTE#$pollID#$optionID"
    }

    object Text {
        fun delegationInfo(toUserID: UserID): String {
            return "You gave your vote to <@$toUserID>"
        }

        fun delegationError(toUserID: UserID): String {
            return "Error! You can't delegate vote to <@$toUserID>. Delegation cycle is found."
        }

        fun pollText(poll: Poll): String {
            return "Vote: ${poll.question}"
        }

        fun originalMessageText(link: String): String {
            return "<$link|View original message>"
        }
    }

    val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val TIME_VALUE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val GSON: Gson = GsonFactory.createSnakeCase()
}
