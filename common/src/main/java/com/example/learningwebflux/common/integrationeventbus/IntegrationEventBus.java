package com.example.learningwebflux.common.integrationeventbus;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@AllArgsConstructor
public class IntegrationEventBus {
    public <TEvent> void publish(TEvent event) {
        var receiverTypes = receiverRegistry.getReceivers(event.getClass());

        for (var receiverType : receiverTypes) {
            var receiveMethod = Arrays.stream(receiverType.getMethods())
                .filter(method -> method.getName().equals("receive"))
                .findFirst()
                .orElseThrow();

            var receiver = applicationContext.getBeansOfType(receiverType).values().toArray()[0];

            Mono<Void> receiveMono;
            try { receiveMono = (Mono<Void>) receiveMethod.invoke(receiver, new Object[]{ event }); }
            catch (Exception e) { throw new RuntimeException(e); }

            receiveMono.subscribe();
        }
    }



    private final IntegrationEventReceiverRegistry receiverRegistry;
    private final ApplicationContext applicationContext;
}
