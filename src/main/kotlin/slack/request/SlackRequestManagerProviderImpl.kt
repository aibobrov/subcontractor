package slack.request

import com.slack.api.methods.AsyncMethodsClient
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.view.View
import core.UIRepresentable
import core.model.base.ChannelID
import slack.model.SlackConversation
import slack.model.SlackUser
import java.util.concurrent.CompletableFuture

class SlackRequestManagerProviderImpl : SlackRequestManagerProvider {
    lateinit var methodsClient: AsyncMethodsClient

    override fun client(methodsClient: AsyncMethodsClient): SlackRequestManagerProvider {
        this.methodsClient = methodsClient
        return this
    }

    override fun openView(view: UIRepresentable<View>, triggerID: String): CompletableFuture<Unit> {
        return methodsClient
            .viewsOpen {
                it
                    .triggerId(triggerID)
                    .view(view.representation())
            }
            .thenApply { Unit }
    }


    override fun pushView(view: UIRepresentable<View>, triggerID: String): CompletableFuture<Unit> {
        return methodsClient
            .viewsPush {
                it
                    .triggerId(triggerID)
                    .view(view.representation())
            }
            .thenApply { Unit }
    }

    override fun postChatMessage(
        view: UIRepresentable<List<LayoutBlock>>,
        channelID: ChannelID
    ): CompletableFuture<Unit> {
        return methodsClient
            .chatPostMessage {
                it
                    .blocks(view.representation())
                    .channel(channelID)
            }
            .thenApply { Unit }
    }

    override fun conversationsList(): CompletableFuture<List<SlackConversation>> {
        return methodsClient
            .conversationsList { it }
            .thenApply { response ->
                response.channels.map {
                    SlackConversation(it.id, "# ${it.nameNormalized}")
                }
            }
    }

    override fun usersList(): CompletableFuture<List<SlackUser>> {
        return methodsClient
            .usersList { it }
            .thenApply { response ->
                response.members
                    .filter { !it.isDeleted }
                    .map {
                        SlackUser(it.id, it.name)
                    }
            }
    }
}
