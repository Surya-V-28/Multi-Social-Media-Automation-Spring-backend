package com.example.learningwebflux.common.integrationeventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IntegrationEventReceiverRegistry {
    public IntegrationEventReceiverRegistry(List<Class<?>> receivers) {
        for (var receiver : receivers) {
            Method method = Arrays
                .stream(receiver.getMethods())
                .filter(element -> element.getName().equals("receive") && element.getParameterTypes()[0] != Object.class)
                .findFirst()
                .orElseThrow();

            Class<?> eventType = method.getParameterTypes()[0];

            if (!this.receivers.containsKey(eventType)) {
                this.receivers.put(eventType, new ArrayList<>());
            }

            this.receivers.get(eventType).add(receiver);
        }
    }

    public List<Class<?>> getReceivers(Class<?> eventType) {
        return receivers.getOrDefault(eventType, List.of());
    }


    private final HashMap<Class<?>, List<Class<?>>> receivers = new HashMap<>();
}
