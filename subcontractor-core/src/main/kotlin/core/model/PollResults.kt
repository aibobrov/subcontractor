package core.model

import core.model.base.OptionID
import kotlinx.serialization.Serializable

@Serializable
data class PollResults(val result: OptionID)