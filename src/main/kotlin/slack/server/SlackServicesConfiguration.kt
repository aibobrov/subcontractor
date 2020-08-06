package slack.server

import core.model.storage.LiquidPollRepository
import core.model.storage.LiquidPollRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import slack.service.SlackPollCreationRepository
import slack.service.SlackPollCreationRepositoryImpl
import slack.service.SlackRequestManagerProviderImpl
import slack.service.SlackRequestProvider

@Configuration
open class SlackServicesConfiguration {

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
        return LiquidPollRepositoryImpl()
    }
}
