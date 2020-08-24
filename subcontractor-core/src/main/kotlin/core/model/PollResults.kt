package core.model

import core.model.base.OptionID

sealed class PollResults {
    data class Option(val result: OptionID) : PollResults()
    data class OptionsList(val list: List<PollResults?>) : PollResults()
}