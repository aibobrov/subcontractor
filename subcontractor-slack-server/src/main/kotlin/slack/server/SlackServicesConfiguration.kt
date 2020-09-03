package slack.server

import core.logic.DataStorage
import core.logic.DataStorageSqlImpl
import core.model.PollResults
import core.model.PollResultsSerializer
import core.model.PollSerializer
import core.model.base.Poll
import core.model.storage.PollInfoStorageImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import service.VotingBusinessLogic
import service.VotingBusinessLogicImpl
import slack.service.*

typealias DataBase = DataStorageSqlImpl<Poll, PollResults>

@Configuration
open class SlackServicesConfiguration {
    private val dispatcherStorage: DataStorage<Poll, PollResults> = DataBase(
        url = Config.DBURL,
        driver = "org.postgresql.Driver",
        user = Config.DBUSERNAME,
        password = Config.DBPASSWORD,
        workSerializer = PollSerializer(),
        workResultsSerializer = PollResultsSerializer()
    )

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

    @Bean
    open fun createDelegationRuleRepository(): SlackDelegationRuleRepository {
        return SlackDelegationRuleRepositoryImpl()
    }
}
