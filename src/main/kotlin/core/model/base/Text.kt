package core.model.base

sealed class Text {
    class Plain(val content: String) : Text()

    class Markdown(val content: String) : Text()

    override fun toString(): String {
        return when (this) {
            is Plain -> content
            is Markdown -> content
        }
    }


}
