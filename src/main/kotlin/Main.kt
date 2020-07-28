import com.slack.api.bolt.App
import com.slack.api.bolt.jetty.SlackAppServer

fun main() {
    val app = App()

    app.command("/liquid") { _, context ->
        context.ack("Hello")
    }

    // don't forget to run `ngrok http 3000` and change urls in slack api
    val server = SlackAppServer(app) // starts server at 3000 port
    server.start()
}
