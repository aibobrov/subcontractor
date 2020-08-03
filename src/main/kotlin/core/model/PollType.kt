package core.model

enum class PollType {
    SINGLE_CHOICE;

    override fun toString(): String {
        return when (this) {
            SINGLE_CHOICE -> "Single Choice"
        }
    }
}
