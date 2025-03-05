package com.example.learningwebflux.notification

import com.example.learningwebflux.common.integrationeventbus.ModuleIntegrationReceiversRegistry
import com.example.learningwebflux.notification.eventreceivers.userregistered.CreateUserOnSignedUpIE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ModuleIntegrationEventReceiversConfiguration {
    @Bean
    open fun notificationModuleIntegrationEventReceiversRegistry(): ModuleIntegrationReceiversRegistry {
        return ModuleIntegrationReceiversRegistry(
            listOf(
                CreateUserOnSignedUpIE::class.java,
            )
        )
    }
}