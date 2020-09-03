package slack.model

sealed class SlackError(message: String?) : Error(message) {
    object EmptyOptions : SlackError("Empty options are not allowed")

    object DelegationRuleMissingUser : SlackError("Select user to delegate vote if needed")

    object DelegationRuleMissingTags : SlackError("Tags shouldn't be empty")

    object AmbiguousPollTags : SlackError("Tags choice is ambiguous")

    object Error : SlackError("Error occurred")

    class AmbiguousRuleWasFound(isDeletion: Boolean) : SlackError("Ambiguous rules was found.${postfix(isDeletion)}") {
        companion object {
            fun postfix(isDeletion: Boolean): String {
                return if (isDeletion)
                    " Theses rules was deleted"
                else ""
            }
        }
    }

}
