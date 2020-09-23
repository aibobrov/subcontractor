package slack.server

import core.logic.DataStorage
import core.logic.DataStorageSqlImpl
import core.model.PollResults
import core.model.PollResultsSerializer

import core.model.storage.PollInfoStorage
import core.model.storage.PollInfoStorageSqlImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import service.VotingBusinessLogic
import service.VotingBusinessLogicImpl
import slack.service.*


@Configuration
open class SlackServicesConfiguration {
    private val dispatcherStorage: DataStorage<PollResults> = DataStorageSqlImpl(
        url = Config.DBURL,
        driver = "org.postgresql.Driver",
        user = Config.DBUSERNAME,
        password = Config.DBPASSWORD,
        workResultsSerializer = PollResultsSerializer()
    )

    private val pollInfoStorage = PollInfoStorageSqlImpl(
        url = Config.DBURL,
        driver = "org.postgresql.Driver",
        user = Config.DBUSERNAME,
        password = Config.DBPASSWORD,
    )

    /* // In-memory version. Debug only
    private val dispatcherStorage = DataStorageTestImpl<PollResults>()
    private val pollInfoStorage = PollInfoStorageTestImpl()
     */
    @Bean
    open fun createSlackProvider(): SlackRequestProvider {
        return SlackRequestManagerProviderImpl()
    }

    @Bean
    open fun createCreationPollRepository(): SlackPollCreationRepository {
        return SlackPollCreationRepositoryImpl()
    }

    @Bean
    open fun createLiquidPollRepository(): PollInfoStorage {
        return pollInfoStorage
    }

    @Bean
    open fun createBusinessLogic(): VotingBusinessLogic {
        return VotingBusinessLogicImpl(dispatcherStorage, pollInfoStorage)
    }

    @Bean
    open fun createDelegationRuleRepository(): SlackDelegationRuleRepository {
        return SlackDelegationRuleRepositoryImpl()
    }
}
