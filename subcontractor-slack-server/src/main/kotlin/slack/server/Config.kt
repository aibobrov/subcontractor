package slack.server

object Config {
    val DBUSERNAME: String = System.getenv("DBUSERNAME")
    val DBPASSWORD: String = System.getenv("DBPASSWORD")
    val DBURL: String = System.getenv("DBURL")
}