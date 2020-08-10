package slack.model

import core.model.base.UserID

data class SlackUserProfile(
    val id: UserID,
    val profileImageURL: String,
    val profileName: String
)
