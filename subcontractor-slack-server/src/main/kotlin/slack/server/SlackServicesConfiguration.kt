package slack.server

import core.logic.DataStorage
import core.logic.DataStorageSqlImpl
import core.logic.DataStorageTestVersion
import core.model.PollResults
import core.model.PollResultsSerializer
import core.model.PollSerializer
import core.model.base.Poll
import core.model.storage.PollInfoStorageImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import service.VotingBusinessLogic
import service.VotingBusinessLogicImpl
import slack.service.SlackPollCreationRepository
import slack.service.SlackPollCreationRepositoryImpl
import slack.service.SlackRequestManagerProviderImpl
import slack.service.SlackRequestProvider

typealias DataBase = DataStorageSqlImpl<Poll, PollResults>

@Configuration
open class SlackServicesConfiguration {

    private val dispatcherStorage : DataStorage<Poll, PollResults> = DataBase("jdbc:postgresql://localhost:5432/test", "org.postgresql.Driver",
        "root", "12345", PollSerializer(), PollResultsSerializer())

    @Bean
    open fun createSlackProvider(): SlackRequestProvider {
        return SlackRequestManagerProviderImpl()
    }

    @Bean
    open fun createCreationPollRepository(): SlackPollCreationRepository {
        return SlackPollCreationRepositoryImpl()
    }

    @Bean
    open fun createLiquidPollRepository(): PollInfoStorageImpl {
        return PollInfoStorageImpl()
    }

    @Bean
    open fun createBusinessLogic(): VotingBusinessLogic {
        return VotingBusinessLogicImpl(dispatcherStorage)
    }
}
