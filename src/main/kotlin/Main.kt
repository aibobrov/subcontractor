import org.springframework.boot.runApplication
import slack.server.SlackApplication

fun main(args: Array<String>) {
    runApplication<SlackApplication>(*args)
}
