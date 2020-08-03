package core.model

import core.model.base.*

data class PollOption(
    override val id: OptionID,
    override val content: Text,
    val description: String? = null
) : Option
