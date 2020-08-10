package slack.service

import com.slack.api.model.block.LayoutBlock
import com.slack.api.methods.AsyncMethodsClient
import com.slack.api.model.view.View
import core.UIRepresentable
import core.model.base.ChannelID
import core.model.base.UserID
import slack.model.SlackAudience
import slack.model.SlackChannel
import slack.model.SlackUser
import utils.combine
import java.util.concurrent.CompletableFuture

interface SlackRequestProvider {
    fun client(methodsClient: AsyncMethodsClient): SlackRequestProvider

    fun openView(view: UIRepresentable<View>, triggerID: String): CompletableFuture<Unit>

    fun pushView(view: UIRepresentable<View>, triggerID: String): CompletableFuture<Unit>

    fun updateView(view: UIRepresentable<View>, viewId: String): CompletableFuture<Unit>

    fun postChatMessage(view: UIRepresentable<List<LayoutBlock>>, channelID: ChannelID): CompletableFuture<Unit>

    fun updateChatMessage(
        view: UIRepresentable<List<LayoutBlock>>,
        channelID: ChannelID,
        ts: String
    ): CompletableFuture<Unit>

    fun postDirectMessage(view: UIRepresentable<List<LayoutBlock>>, userID: UserID): CompletableFuture<Unit>

    fun conversationsList(): CompletableFuture<List<SlackChannel>>

    fun usersList(): CompletableFuture<List<SlackUser>>

    fun audienceList(): CompletableFuture<SlackAudience> {
        return usersList().combine(conversationsList()).thenApply {
            SlackAudience(it.first, it.second)
        }
    }
}
