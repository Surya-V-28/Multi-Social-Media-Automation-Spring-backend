package com.example.learningwebflux.post.restapi.routes.fulfillscheduledpost;

import com.example.learningwebflux.post.commands.fulfillscheduledpost.FulfillScheduledPostCommandHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class FulfillScheduledPostRouteHandler {
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        return serverRequest
            .bodyToMono(FulfillScheduledPostRouteRequestBody.class)
            .flatMap(requestBody -> commandHandler.perform(requestBody.id()))
            .then(ServerResponse.ok().build());
    }


    private final FulfillScheduledPostCommandHandler commandHandler;
}
