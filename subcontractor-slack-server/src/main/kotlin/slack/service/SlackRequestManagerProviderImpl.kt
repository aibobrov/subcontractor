package slack.service

import com.slack.api.methods.AsyncMethodsClient
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.view.View
import core.UIRepresentable
import core.model.base.ChannelID
import core.model.base.UserID
import slack.model.SlackConversation
import slack.model.SlackUser
import java.util.concurrent.CompletableFuture

class SlackRequestManagerProviderImpl : SlackRequestProvider {
    lateinit var methodsClient: AsyncMethodsClient

    override fun client(methodsClient: AsyncMethodsClient): SlackRequestProvider {
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

    override fun updateView(view: UIRepresentable<View>, viewId: String): CompletableFuture<Unit> {
        return methodsClient
            .viewsUpdate {
                it
                    .viewId(viewId)
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
        blocks: UIRepresentable<List<LayoutBlock>>,
        channelID: ChannelID
    ): CompletableFuture<Unit> {
        return methodsClient
            .chatPostMessage {
                it
                    .blocks(blocks.representation())
                    .channel(channelID)
            }
            .thenApply { Unit }
    }

    override fun updateChatMessage(
        blocks: UIRepresentable<List<LayoutBlock>>,
        channelID: ChannelID,
        ts: String
    ): CompletableFuture<Unit> {
        return methodsClient
            .chatUpdate {
                it
                    .ts(ts)
                    .channel(channelID)
                    .blocks(blocks.representation())
            }
            .thenApply { Unit }
    }

    override fun postDirectMessage(blocks: UIRepresentable<List<LayoutBlock>>, userID: UserID): CompletableFuture<Unit> {
        return methodsClient
            .conversationsOpen {
                it.users(listOf(userID))
            }
            .thenCompose { response ->
                postChatMessage(blocks, response.channel.id)
            }
    }

    override fun conversationsList(): CompletableFuture<List<SlackConversation>> {
        return methodsClient
            .conversationsList { it }
            .thenApply { response ->
                response.channels.map {
                    SlackConversation(it.id)
                }
            }
    }

    override fun usersList(): CompletableFuture<List<SlackUser>> {
        return methodsClient
            .usersList { it }
            .thenApply { response ->
                response.members
                    .filter { !it.isDeleted && !it.isBot }
                    .map {
                        SlackUser(it.id, it.name)
                    }
            }
    }
}
