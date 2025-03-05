package com.example.learningwebflux.post.restapi.routes.removescheduledpost;

import com.example.learningwebflux.post.commands.removescheduledpost.RemoveScheduledPostCommandHandler;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class RemoveScheduledPostRouteHandler {
    public Mono<ServerResponse> handle(ServerRequest request) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(securityContext -> {
                var userId = securityContext.getAuthentication().getName();
                var postId = request.pathVariable("id");

                return commandHandler.perform(userId, postId);
            })
            .then(ServerResponse.ok().build());
    }


    private final RemoveScheduledPostCommandHandler commandHandler;
}