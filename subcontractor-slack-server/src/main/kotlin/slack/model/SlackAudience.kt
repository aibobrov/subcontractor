package slack.model

data class SlackAudience(
    val users: List<SlackUser>,
    val channel: List<SlackChannel>
)

