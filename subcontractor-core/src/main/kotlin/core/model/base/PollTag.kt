package core.model.base

/// Think about bit mask and a bunch of singleton tags
data class PollTag(val name: String)


object StandardTag {
    val GENERAL = PollTag("general")
    val SOFTWARE = PollTag("software")
    val HARDWARE = PollTag("hardware")
    val TESTING = PollTag("testing")

    val ALL = listOf(
        GENERAL,
        SOFTWARE,
        HARDWARE,
        TESTING
    )
}
