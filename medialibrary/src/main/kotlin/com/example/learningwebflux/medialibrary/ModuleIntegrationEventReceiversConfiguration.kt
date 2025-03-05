package com.example.learningwebflux.medialibrary

import com.example.learningwebflux.common.integrationeventbus.ModuleIntegrationReceiversRegistry
import com.example.learningwebflux.medialibrary.eventreceivers.userregistered.CreateUserOnSignedUpIE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ModuleIntegrationEventReceiversConfiguration {
    @Bean
    open fun mediaLibraryModuleIntegrationEventReceiversRegistry(): ModuleIntegrationReceiversRegistry {
        return ModuleIntegrationReceiversRegistry(
            listOf(
                CreateUserOnSignedUpIE::class.java,

            )
        )
    }
}