package com.example.learningwebflux.platformconnection;

import com.example.learningwebflux.platformconnection.platformconnection.repository.PlatformConnectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class PlatformConnectionApi {
    public Mono<Map<String, Boolean>> checkConnectionsValidity(List<String> connectionIds) {
        return Flux.fromIterable(connectionIds)
            .flatMap(platformConnectionRepository::getWithId)
            .collectMap(e -> e.id, e -> e.getAccessToken() != null);
    }


    private final PlatformConnectionRepository platformConnectionRepository;
}
