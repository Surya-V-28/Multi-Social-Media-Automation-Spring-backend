package com.example.learningwebflux.platformconnection.restapi;

import com.example.learningwebflux.platformconnection.restapi.routes.connecttoplatform.ConnectToPlatformRouteHandler;
import com.example.learningwebflux.platformconnection.restapi.routes.exchangeauthorizationcodeforaccesstoken.ExchangeAuthorizationCodeForAccessTokenRouteHandler;
import com.example.learningwebflux.platformconnection.restapi.routes.facebook.oauthdialogcallback.FacebookOAuthDialogCallbackRouteHandler;
import com.example.learningwebflux.platformconnection.restapi.routes.getplatformconnection.GetPlatformConnectionRouteHandler;
import com.example.learningwebflux.platformconnection.restapi.routes.getuserplatformconnections.GetUserPlatformConnectionsRouteHandler;
import com.example.learningwebflux.platformconnection.restapi.routes.removeplatformconnection.RemovePlatformConnectionRouteHandler;
import com.example.learningwebflux.platformconnection.scheduledtasks.RefreshAllPlatformConnectionsScheduledTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouteConfiguration {
    @Bean
    RouterFunction<ServerResponse> platformConnectionRouterFunction(
        GetUserPlatformConnectionsRouteHandler getUserPlatformConnectionsRouteHandler,
        ConnectToPlatformRouteHandler connectToPlatformRouteHandler,
        GetPlatformConnectionRouteHandler getPlatformConnectionRouteHandler,
        RemovePlatformConnectionRouteHandler removePlatformConnectionRouteHandler,

        ExchangeAuthorizationCodeForAccessTokenRouteHandler exchangeAuthorizationCodeForAccessTokenRouteHandler,

        FacebookOAuthDialogCallbackRouteHandler facebookOAuthDialogCallbackRouteHandler,
        RefreshAllPlatformConnectionsScheduledTask refreshAllPlatformConnectionsScheduledTask
    ) {
        return RouterFunctions
            .nest(
                path("/api/platform-connection"),
                RouterFunctions
                    .nest(
                        path("/{userId}/connections"),
                        RouterFunctions
                            .route(POST(""), connectToPlatformRouteHandler::handle)
                            .andRoute(GET(""), getUserPlatformConnectionsRouteHandler::handle)
                            .andRoute(GET("/{id}"), getPlatformConnectionRouteHandler::handle)
                            .andRoute(DELETE("/{id}"), removePlatformConnectionRouteHandler::handle)
                    )
                    .andRoute(POST("/exchange-authorization-code-for-access-token"), exchangeAuthorizationCodeForAccessTokenRouteHandler::handle)
                    .andNest(
                        path("/facebook"),
                        RouterFunctions
                            .route(GET("/oauth-dialog-callback"), facebookOAuthDialogCallbackRouteHandler::handle)
                    )
                    .andRoute(
                        POST("/refresh-all"),
                        (request) -> {
                            return refreshAllPlatformConnectionsScheduledTask._refreshConnections()
                                .then(Mono.defer(() -> {
                                    var responseBody = Map.of(
                                        "message", "Refreshed all platform connections"
                                    );

                                    return ServerResponse
                                        .ok()
                                        .body(BodyInserters.fromValue(responseBody));
                                }));
                        }
                    )
            );
    }
}
