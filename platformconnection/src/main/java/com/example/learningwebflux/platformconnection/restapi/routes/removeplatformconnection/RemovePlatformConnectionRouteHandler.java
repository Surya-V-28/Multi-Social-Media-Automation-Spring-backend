package com.example.learningwebflux.platformconnection.restapi.routes.removeplatformconnection;

import com.example.learningwebflux.platformconnection.commands.removeplatformconnection.RemovePlatformConnectionCommandHandler;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class RemovePlatformConnectionRouteHandler {
    public Mono<ServerResponse> handle(ServerRequest request) {
        var userId = request.pathVariable("userId");
        var platformConnectionId = request.pathVariable("id");

        return commandHandler.perform(userId, platformConnectionId)
            .then(ServerResponse.ok().build());
    }


    private final RemovePlatformConnectionCommandHandler commandHandler;
}
