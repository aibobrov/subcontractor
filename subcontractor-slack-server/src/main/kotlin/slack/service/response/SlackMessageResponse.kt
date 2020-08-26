package slack.service.response

import core.model.PollCreationTime
import core.model.PollVoter

data class SlackMessageResponse(val voter: PollVoter, val time: PollCreationTime)