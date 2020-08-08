package slack.server.base

import com.slack.api.bolt.context.builtin.ActionContext
import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.context.builtin.ViewSubmissionContext
import com.slack.api.bolt.request.builtin.BlockActionRequest
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest

interface SlackDataFactory<Request, Context, Data> {
    fun fromRequest(request: Request, context: Context): Data
}

typealias SlackViewSubmissionDataFactory<Data> = SlackDataFactory<ViewSubmissionRequest, ViewSubmissionContext, Data>
typealias SlackBlockActionDataFactory<Data> = SlackDataFactory<BlockActionRequest, ActionContext, Data>
typealias SlackSlashCommandDataFactory<Data> = SlackDataFactory<SlashCommandRequest, SlashCommandContext, Data>
