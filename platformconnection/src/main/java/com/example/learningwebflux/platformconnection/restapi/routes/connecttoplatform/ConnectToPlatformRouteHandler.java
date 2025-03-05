package com.example.learningwebflux.platformconnection.restapi.routes.connecttoplatform;

import com.example.learningwebflux.platformconnection.commands.connecttoplatform.ConnectToPlatformCommandHandler;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class ConnectToPlatformRouteHandler {
    public Mono<ServerResponse> handle(ServerRequest request) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(securityContext -> {
                return request.bodyToMono(ConnectToPlatformRouteRequestBody.class)
                    .flatMap(requestBody -> {
                        return commandHandler.perform(
                            request.pathVariable("userId"),
                            requestBody.platform(),
                            requestBody.accessToken(),
                            requestBody.refreshToken(),
                            requestBody.expiresAt()
                        );
                    });
            })
            .flatMap(id -> {
                return ServerResponse.ok()
                    .body( BodyInserters.fromValue(new ResponseBody(id)) );
            });
    }



    private final ConnectToPlatformCommandHandler commandHandler;
}

record ResponseBody(String id) { }