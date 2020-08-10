package slack.model

import core.model.AgreeDisagreePoll
import core.model.PollAuthor
import core.model.PollType
import core.model.base.PollID

object BuilderFactory {
    fun createBuilder(builder: SlackPollBuilder, newType: PollType): SlackPollBuilder {
        return createBuilder(builder.id, builder.author, newType).apply { with(builder) }
    }


    fun createBuilder(id: PollID, author: PollAuthor, type: PollType): SlackPollBuilder {
        return when (type) {
            PollType.SINGLE_CHOICE -> SlackMutablePollBuilder(id, author, type)
            PollType.AGREE_DISAGREE -> SlackImmutableOptionPollBuilder(id, author, type, AgreeDisagreePoll.OPTIONS)
        }
    }
}
