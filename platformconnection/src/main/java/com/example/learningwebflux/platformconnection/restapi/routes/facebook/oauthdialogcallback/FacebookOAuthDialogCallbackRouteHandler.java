package com.example.learningwebflux.platformconnection.restapi.routes.facebook.oauthdialogcallback;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class FacebookOAuthDialogCallbackRouteHandler {
    public Mono<ServerResponse> handle(ServerRequest request) {
        StringBuilder responseBody = new StringBuilder();

        for (var entry : request.queryParams().toSingleValueMap().entrySet()) {
            responseBody.append(String.format("%s = %s\n", entry.getKey(), entry.getValue()));
        }

        return ServerResponse.ok()
            .body(BodyInserters.fromValue(responseBody.toString()));
    }
}
