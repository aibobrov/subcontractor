package slack.model

data class SlackAudience(
    val conversations: List<SlackConversation>
): List<SlackConversation> by conversations {
    companion object {
        val EMPTY = SlackAudience(listOf())
    }
}

