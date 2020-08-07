package slack.model

data class PollAdvancedOption(
    val showResponses: Boolean,
    val startDateTimeEnabled: Boolean,
    val finishDateTimeEnabled: Boolean,
    val isAnonymous: Boolean
)
