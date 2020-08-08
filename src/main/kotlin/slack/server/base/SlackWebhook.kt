package slack.server.base

import com.slack.api.bolt.App

// TODO: rename interface
interface AppRegistrable {
    fun registerIn(app: App)
}

interface SlackMetadataWebhook<Content, Metadata> : AppRegistrable {
    fun handle(metadata: Metadata, content: Content)
}

interface SlackWebhook<Content> : AppRegistrable {
    fun handle(content: Content)
}
