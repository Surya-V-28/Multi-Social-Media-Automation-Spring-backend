package com.example.learningwebflux.authentication.restapi.routes;

import com.example.learningwebflux.authentication.restapi.routes.confirm.ConfirmRouteHandler;
import com.example.learningwebflux.authentication.restapi.routes.login.LoginRouteHandler;
import com.example.learningwebflux.authentication.restapi.routes.me.MeRouteHandler;
import com.example.learningwebflux.authentication.restapi.routes.signup.SignUpRouteHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouteConfiguration {
    @Bean
    public RouterFunction<ServerResponse> AuthenticationRouterFunction(
        SignUpRouteHandler signUpRouteHandler,
        ConfirmRouteHandler confirmRouteHandler,
        LoginRouteHandler loginRouteHandler,
        MeRouteHandler meRouteHandler
    ) {
        return RouterFunctions
            .nest(
                RequestPredicates.path("/api/auth"),
                RouterFunctions
                    .route(POST("/signup").and(accept(MediaType.APPLICATION_JSON)), signUpRouteHandler::handle)
                    .andRoute(POST("/confirm").and(accept(MediaType.APPLICATION_JSON)), confirmRouteHandler::handle)
                    .andRoute(POST("/login").and(accept(MediaType.APPLICATION_JSON)), loginRouteHandler::handle)
                    .andRoute(GET("/me"), meRouteHandler::handle)
            );
    }
}