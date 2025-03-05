package com.example.learningwebflux.platformconnection;

import com.example.learningwebflux.common.integrationeventbus.ModuleIntegrationReceiversRegistry;
import com.example.learningwebflux.platformconnection.integrationeventreceivers.usersignedup.CreateCustomerIEReceiver;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModuleIntegrationEventReceiversConfiguration {
    @Bean
    ModuleIntegrationReceiversRegistry platformConnectionModuleIntegrationEventReceiversRegistry() {
        return new ModuleIntegrationReceiversRegistry(
            List.of(
                CreateCustomerIEReceiver.class
            )
        );
    }
}
