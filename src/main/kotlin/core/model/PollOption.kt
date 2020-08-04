package core.model

import core.model.base.*

data class PollOption(
    override val id: OptionID,
    override val content: String,
    val description: String? = null
) : Option
