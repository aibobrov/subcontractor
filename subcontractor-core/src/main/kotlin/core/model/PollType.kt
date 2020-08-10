package core.model

enum class PollType {
    SINGLE_CHOICE,
    AGREE_DISAGREE;

    override fun toString(): String {
        return when (this) {
            SINGLE_CHOICE -> "Single Choice"
            AGREE_DISAGREE -> "Agree/Disagree"
        }
    }

    companion object {
        val DEFAULT: PollType = SINGLE_CHOICE
    }
}
