package com.example.learningwebflux;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventBus;
import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiverRegistry;
import com.example.learningwebflux.common.integrationeventbus.ModuleIntegrationReceiversRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class IntegrationEventBusConfiguration {
    @Bean
    public IntegrationEventBus integrationEventBus(
        List<ModuleIntegrationReceiversRegistry> moduleReceiverRegistries,
        ApplicationContext applicationContext
    ) {
        var receivers = new ArrayList<Class<?>>();
        for (var moduleReceiverRegistry : moduleReceiverRegistries) {
            receivers.addAll(moduleReceiverRegistry.getReceivers());
        }

        var integrationEventBus = new IntegrationEventBus(new IntegrationEventReceiverRegistry(receivers), applicationContext);
        return integrationEventBus;
    }
}
