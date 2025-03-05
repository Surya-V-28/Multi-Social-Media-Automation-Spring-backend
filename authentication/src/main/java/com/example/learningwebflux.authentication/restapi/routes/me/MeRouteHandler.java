package com.example.learningwebflux.authentication.restapi.routes.me;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class MeRouteHandler {
    public Mono<ServerResponse> handle(ServerRequest request) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(securityContext -> {
                var responseBody = Map.of(
                    "id", (String) securityContext.getAuthentication().getPrincipal()
                );

                return ServerResponse.ok()
                    .body(BodyInserters.fromValue(responseBody));
            });
    }
}
