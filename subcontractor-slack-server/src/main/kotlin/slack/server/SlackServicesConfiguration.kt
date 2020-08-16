package slack.server

import core.logic.DataStorage
import core.logic.DataStorageTestVersion
import core.model.storage.LiquidPollRepository
import core.model.storage.LiquidPollRepositoryImpl
import core.model.storage.PollCreationTimesStorage
import core.model.storage.PollCreationTimesStorageImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import service.VotingBusinessLogic
import service.VotingBusinessLogicImpl
import slack.service.*

typealias DataBase = DataStorageTestVersion

@Configuration
open class SlackServicesConfiguration {

    val storage : DataStorage = DataBase()

    @Bean
    open fun createSlackProvider(): SlackRequestProvider {
        return SlackRequestManagerProviderImpl()
    }

    @Bean
    open fun createCreationPollRepository(): SlackPollCreationRepository {
        return SlackPollCreationRepositoryImpl()
    }

    @Bean
    open fun createLiquidPollRepository(): LiquidPollRepository {
        return LiquidPollRepositoryImpl(storage, PollCreationTimesStorageImpl())
    }

    @Bean
    open fun createBusinessLogic(): VotingBusinessLogic {
        return VotingBusinessLogicImpl(storage)
    }
}
