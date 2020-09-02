package core.model

import core.model.base.Option
import core.model.base.OptionID
import kotlinx.serialization.Serializable

@Serializable
data class PollOption(
    override val id: OptionID,
    override val content: String,
    val description: String? = null
) : Option
