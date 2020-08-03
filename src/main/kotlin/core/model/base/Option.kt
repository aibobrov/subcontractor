package core.model.base

import core.model.base.OptionID
import core.model.base.Text

interface Option {
    val id: OptionID

    val content: Text
}
