package slack.service

import com.slack.api.methods.AsyncMethodsClient
import com.slack.api.model.Attachment
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.view.View
import core.UIRepresentable
import core.model.PollVoter
import core.model.base.ChannelID
import core.model.base.UserID
import core.model.base.VotingTime
import slack.model.SlackUserProfile
import slack.model.SlackUser
import slack.service.response.SlackMessageResponse
import utils.unreachable
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

interface SlackRequestProvider {
    fun client(methodsClient: AsyncMethodsClient): SlackRequestProvider

    fun openView(view: UIRepresentable<View>, triggerID: String): CompletableFuture<Unit>

    fun pushView(view: UIRepresentable<View>, triggerID: String): CompletableFuture<Unit>

    fun updateView(view: UIRepresentable<View>, viewId: String): CompletableFuture<Unit>

    fun postChatMessage(
        text: String?,
        blocks: UIRepresentable<List<LayoutBlock>>,
        channelID: ChannelID
    ): CompletableFuture<SlackMessageResponse?>


    fun postEphemeral(
        text: String?,
        attachment: UIRepresentable<Attachment>,
        channelID: ChannelID,
        userID: UserID
    ): CompletableFuture<Unit>

    fun postEphemeral(
        text: String?,
        channelID: ChannelID,
        userID: UserID
    ): CompletableFuture<Unit>

    fun scheduleChatMessage(
        text: String?,
        blocks: UIRepresentable<List<LayoutBlock>>,
        channelID: ChannelID,
        postAt: LocalDateTime
    ): CompletableFuture<SlackMessageResponse?>

    fun updateChatMessage(
        blocks: UIRepresentable<List<LayoutBlock>>,
        channelID: ChannelID,
        ts: String
    ): CompletableFuture<Unit>

    fun postDirectMessage(
        text: String?,
        blocks: UIRepresentable<List<LayoutBlock>>,
        userID: UserID
    ): CompletableFuture<SlackMessageResponse?>

    fun conversationsList(): CompletableFuture<List<PollVoter>>

    fun usersList(): CompletableFuture<List<SlackUser>>

    fun usersList(channelID: ChannelID): CompletableFuture<List<SlackUser>?>

    fun userProfile(userID: UserID): CompletableFuture<SlackUserProfile>

    fun userProfiles(userIDs: Set<String>): CompletableFuture<Map<UserID, SlackUserProfile>>

    fun sendChatMessage(
        text: String?,
        blocks: UIRepresentable<List<LayoutBlock>>,
        channelID: ChannelID,
        votingTime: VotingTime
    ): CompletableFuture<SlackMessageResponse?> {
        return when (votingTime) {
            VotingTime.Unlimited, is VotingTime.UpTo -> postChatMessage(text, blocks, channelID)
            is VotingTime.ScheduledTime -> scheduleChatMessage(text, blocks, channelID, votingTime.startDateTime)
            else -> unreachable()
        }
    }

    fun getPermanentMessageURL(
        channelID: ChannelID,
        ts: String
    ): CompletableFuture<String>

}
