package com.example.learningwebflux;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Component
public class RouteConfiguration {
    @Bean
    public RouterFunction<ServerResponse> routerFunctions() {
        return RouterFunctions
            .route( GET("/ping"), (serverRequest) -> ServerResponse.ok().body(BodyInserters.fromValue("pong")) );
    }
}