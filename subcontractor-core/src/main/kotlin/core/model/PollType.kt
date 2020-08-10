package core.model

enum class PollType {
    SINGLE_CHOICE,
    AGREE_DISAGREE,
    ONE_TO_N;

    override fun toString(): String {
        return when (this) {
            SINGLE_CHOICE -> "Single Choice"
            AGREE_DISAGREE -> "Agree/Disagree"
            ONE_TO_N -> "One to N"
        }
    }

    companion object {
        val DEFAULT: PollType = SINGLE_CHOICE
    }
}
