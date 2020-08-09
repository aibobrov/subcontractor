package slack.model

object SlackPollBuilderValidator {
    fun validate(builder: SlackPollBuilder): List<SlackError> {
        fun checkOptions(): SlackError? {
            if (builder.options.isEmpty())
                return SlackError.EmptyOptions
            return null
        }

        return listOfNotNull(checkOptions())
    }
}
