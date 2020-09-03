package slack.model

import core.model.base.DelegationRule

object SlackValidator {
    fun validate(builder: SlackPollBuilder): List<SlackError> {
        fun checkOptions(): SlackError? {
            if (builder.options.isEmpty())
                return SlackError.EmptyOptions
            return null
        }

        return listOfNotNull(checkOptions())
    }

    fun validate(builder: DelegationRule.Builder, otherRules: List<DelegationRule>): List<SlackError> {
        val errors = mutableListOf<SlackError>()

        if (builder.tags.isEmpty())
            errors.add(SlackError.DelegationRuleMissingTags)

        if (builder.toUserID == null)
            errors.add(SlackError.DelegationRuleMissingUser)

        val isAmbiguous = otherRules.firstOrNull { it.tags == builder.tags } != null
        if (isAmbiguous)
            errors.add(SlackError.AmbiguousPollTags)

        return errors
    }
}
