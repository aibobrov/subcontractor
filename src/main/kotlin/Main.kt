import org.springframework.boot.runApplication
import slack.service.SlackRequestProvider
import slack.service.SlackRequestManagerProviderImpl
import slack.server.SlackApplication


val provider: SlackRequestProvider = SlackRequestManagerProviderImpl()
fun main(args: Array<String>) {
    runApplication<SlackApplication>(*args)
}
//fun main() {
//    runApplication<SlackApplication>()
//    val app = App()
//

//
//    app.blockAction("single-option-poll-add") { req, context ->
//        context.client().viewsPush { requestBuilder ->
//            val tmp = AddOptionsPollView(MOCK.OPTIONS.map { it.content.toString() })
//
//            requestBuilder
//                .view(tmp.representation())
//                .triggerId(context.triggerId)
//        }
//        context.ack()
//    }

// don't forget to run `ngrok http 3000` and change urls in slack api
//    val server = SlackAppServer(app) // starts server at 3000 port
//    server.start()
//}
