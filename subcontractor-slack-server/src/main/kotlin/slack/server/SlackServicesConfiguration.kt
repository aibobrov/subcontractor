package slack.server

import core.logic.DataStorage
import core.logic.DataStorageTestVersion
import core.model.base.OptionID
import core.model.base.Poll
import core.model.storage.PollCreationTimesStorageImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import service.VotingBusinessLogic
import service.VotingBusinessLogicImpl
import slack.service.*

typealias DataBase = DataStorageTestVersion<Poll, OptionID>

@Configuration
open class SlackServicesConfiguration {

    val storage : DataStorage<Poll, OptionID> = DataBase()

    @Bean
    open fun createSlackProvider(): SlackRequestProvider {
        return SlackRequestManagerProviderImpl()
    }

    @Bean
    open fun createCreationPollRepository(): SlackPollCreationRepository {
        return SlackPollCreationRepositoryImpl()
    }

    @Bean
    open fun createLiquidPollRepository(): PollCreationTimesStorageImpl {
        return PollCreationTimesStorageImpl()
    }

    @Bean
    open fun createBusinessLogic(): VotingBusinessLogic {
        return VotingBusinessLogicImpl(storage)
    }
}
