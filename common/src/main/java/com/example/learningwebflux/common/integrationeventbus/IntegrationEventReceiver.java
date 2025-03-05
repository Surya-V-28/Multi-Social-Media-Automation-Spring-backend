package com.example.learningwebflux.common.integrationeventbus;

import reactor.core.publisher.Mono;

public interface IntegrationEventReceiver<TEvent> {
    public Mono<Void> receive(TEvent event);
}
