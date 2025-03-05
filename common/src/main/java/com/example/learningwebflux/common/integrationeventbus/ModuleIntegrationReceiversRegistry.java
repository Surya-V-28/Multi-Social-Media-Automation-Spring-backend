package com.example.learningwebflux.common.integrationeventbus;


import java.util.List;

public class ModuleIntegrationReceiversRegistry {
    public ModuleIntegrationReceiversRegistry(List<Class<?>> receivers) {
        this.receivers = receivers;
    }

    public List<Class<?>> getReceivers() {
        return this.receivers;
    }


    private final List<Class<?>> receivers;
}