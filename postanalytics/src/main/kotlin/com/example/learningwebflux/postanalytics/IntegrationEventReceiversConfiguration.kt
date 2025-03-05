package com.example.learningwebflux.postanalytics

import com.example.learningwebflux.common.integrationeventbus.ModuleIntegrationReceiversRegistry
import com.example.learningwebflux.postanalytics.iereceivers.connectedtoplatform.CreatePlatformConnectionIEReceiver
import com.example.learningwebflux.postanalytics.iereceivers.scheduledpost.CreateScheduledPostIEReceiver
import com.example.learningwebflux.postanalytics.iereceivers.usersignedup.CreateUserIEReceiver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class IntegrationEventReceiversConfiguration {
    @Bean
    open fun postAnalyticsModuleIntegrationEventBusReceiversRegistry(): ModuleIntegrationReceiversRegistry {
        val integrationEventReceivers = listOf(
            CreatePlatformConnectionIEReceiver::class.java,
            CreateScheduledPostIEReceiver::class.java,
            CreateUserIEReceiver::class.java,
        )

        return ModuleIntegrationReceiversRegistry(integrationEventReceivers)
    }
}