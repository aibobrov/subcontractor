package slack.model

sealed class SlackError(message: String?) : Error(message) {
    object EmptyOptions : SlackError("Empty options are not allowed")

    data class Multiple(val errors: List<SlackError>) : SlackError("Multiple errors")
}
