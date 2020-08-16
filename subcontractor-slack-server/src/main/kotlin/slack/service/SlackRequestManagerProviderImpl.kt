package slack.service

import com.slack.api.methods.AsyncMethodsClient
import com.slack.api.model.Attachment
import com.slack.api.model.Attachments.attachment
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.view.View
import core.UIRepresentable
import core.model.PollCreationTime
import core.model.base.ChannelID
import core.model.base.UserID
import core.model.SlackConversation
import slack.model.SlackUser
import slack.model.SlackUserProfile
import utils.unixEpochTimestamp
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import slack.ui.poll.PreviewPollAttachmentBlockView


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
        text: String?,
        blocks: UIRepresentable<List<LayoutBlock>>,
        channelID: ChannelID
    ): CompletableFuture<Pair<SlackConversation, PollCreationTime>?> {
        return methodsClient
            .chatPostMessage {
                it
                    .text(text)
                    .blocks(blocks.representation())
                    .channel(channelID)
            }
            .thenApply {response -> Pair(SlackConversation(response.channel), PollCreationTime(response.ts))}
    }

    override fun postEphemeral(
        text: String?,
        attachment: UIRepresentable<Attachment>,
        channelID: ChannelID,
        userID: UserID
    ): CompletableFuture<Unit> {
        return methodsClient
            .chatPostEphemeral { builder ->
                builder
                    .text(text)
                    .channel(channelID)
                    .attachments(listOf(attachment.representation()))
                    .user(userID)
            }
            .thenApply { Unit }
    }

    override fun scheduleChatMessage(
        text: String?,
        blocks: UIRepresentable<List<LayoutBlock>>,
        channelID: ChannelID,
        postAt: LocalDateTime
    ): CompletableFuture<Pair<SlackConversation, PollCreationTime>?> {
        return methodsClient
            .chatScheduleMessage {
                it
                    .text(text)
                    .blocks(blocks.representation())
                    .channel(channelID)
                    .postAt(postAt.unixEpochTimestamp.toInt())
            }
            .thenCompose {
                if (it.error == "time_in_past")
                    return@thenCompose postChatMessage(text, blocks, channelID)
                CompletableFuture.completedFuture(Pair(SlackConversation(it.channel), PollCreationTime(it.postAt.toString())))
            }
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

    override fun postDirectMessage(
        text: String?,
        blocks: UIRepresentable<List<LayoutBlock>>,
        userID: UserID
    ): CompletableFuture<Pair<SlackConversation, PollCreationTime>?> {
        return methodsClient
            .conversationsOpen {
                it.users(listOf(userID))
            }
            .thenCompose { response ->
                postChatMessage(text, blocks, response.channel.id)
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
                        SlackUser(it.id)
                    }
            }
    }

    override fun usersList(channelID: ChannelID): CompletableFuture<List<SlackUser>?> {
        return methodsClient
            .conversationsMembers { it.channel(channelID) }
            .thenApply { response ->
                response.members?.map { SlackUser(it) }
            }
    }

    override fun userProfile(userID: UserID): CompletableFuture<SlackUserProfile> {
        return methodsClient
            .usersProfileGet { it.user(userID) }
            .thenApply {
                SlackUserProfile(userID, it.profile.image32, it.profile.realName)
            }
    }

    override fun userProfiles(userIDs: Set<String>): CompletableFuture<Map<UserID, SlackUserProfile>> {
        val userProfiles = userIDs.map(this::userProfile)
        val allOf = CompletableFuture.allOf(*userProfiles.toTypedArray())
        return allOf
            .thenApply { _ ->
                userProfiles
                    .map {
                        val profile = it.get()
                        profile.id to profile
                    }
                    .toMap()
            }
    }

    override fun getPermanentMessageURL(channelID: ChannelID, ts: String): CompletableFuture<String> {
        return methodsClient
            .chatGetPermalink {
                it
                    .channel(channelID)
                    .messageTs(ts)
            }
            .thenApply {
                it.permalink
            }
    }

}
