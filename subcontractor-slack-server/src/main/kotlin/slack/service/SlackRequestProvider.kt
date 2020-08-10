package slack.service

import com.slack.api.methods.AsyncMethodsClient
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.view.View
import core.UIRepresentable
import core.model.base.ChannelID
import core.model.base.UserID
import slack.model.SlackConversation
import slack.model.SlackUserProfile
import slack.model.SlackUser
import java.util.concurrent.CompletableFuture

interface SlackRequestProvider {
    fun client(methodsClient: AsyncMethodsClient): SlackRequestProvider

    fun openView(view: UIRepresentable<View>, triggerID: String): CompletableFuture<Unit>

    fun pushView(view: UIRepresentable<View>, triggerID: String): CompletableFuture<Unit>

    fun updateView(view: UIRepresentable<View>, viewId: String): CompletableFuture<Unit>

    fun postChatMessage(blocks: UIRepresentable<List<LayoutBlock>>, channelID: ChannelID): CompletableFuture<Unit>

    fun updateChatMessage(
        blocks: UIRepresentable<List<LayoutBlock>>,
        channelID: ChannelID,
        ts: String
    ): CompletableFuture<Unit>

    fun postDirectMessage(blocks: UIRepresentable<List<LayoutBlock>>, userID: UserID): CompletableFuture<Unit>

    fun conversationsList(): CompletableFuture<List<SlackConversation>>

    fun usersList(): CompletableFuture<List<SlackUser>>

    fun userProfile(userID: UserID): CompletableFuture<SlackUserProfile>

    fun userProfiles(userIDs: List<String>): CompletableFuture<Map<UserID, SlackUserProfile>>
}
