package slack.server.base

import com.slack.api.bolt.App

interface RegistrableWebhook {
    fun registerIn(app: App)
}

interface SlackMetadataWebhook<Content, Metadata> : RegistrableWebhook {
    fun handle(metadata: Metadata, content: Content)
}

interface SlackWebhook<Content> : RegistrableWebhook {
    fun handle(content: Content)
}
