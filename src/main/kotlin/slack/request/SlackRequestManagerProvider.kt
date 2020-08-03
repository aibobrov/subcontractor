package slack.request

import com.slack.api.model.block.LayoutBlock
import com.slack.api.methods.AsyncMethodsClient
import com.slack.api.model.view.View
import core.UIRepresentable
import core.model.base.ChannelID
import slack.model.SlackConversation
import slack.model.SlackUser
import java.util.concurrent.CompletableFuture

interface SlackRequestManagerProvider {
    fun client(methodsClient: AsyncMethodsClient): SlackRequestManagerProvider

    fun openView(view: UIRepresentable<View>, triggerID: String): CompletableFuture<Unit>

    fun pushView(view: UIRepresentable<View>, triggerID: String): CompletableFuture<Unit>

    fun postChatMessage(view: UIRepresentable<List<LayoutBlock>>, channelID: ChannelID): CompletableFuture<Unit>

    fun conversationsList(): CompletableFuture<List<SlackConversation>>

    fun usersList(): CompletableFuture<List<SlackUser>>
}
