package com.example.learningwebflux.authentication.restapi.routes.login;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;

import java.util.Map;

@Component
public class LoginRouteHandler {
    public LoginRouteHandler(
        CognitoIdentityProviderAsyncClient cognitoClient,
        @Value("${aws.cognito.clientId}") String clientId
    ) {
        this.cognitoClient = cognitoClient;
        this.clientId = clientId;
    }

    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.bodyToMono(LoginRouteRequestBody.class)
            .flatMap(requestBody -> {
                var initiateAuthRequest = InitiateAuthRequest.builder()
                    .clientId(clientId)
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .authParameters(
                        Map.of(
                            "USERNAME", requestBody.email(),
                            "PASSWORD", requestBody.password()
                        )
                    )
                    .build();

                return Mono.fromFuture(cognitoClient.initiateAuth(initiateAuthRequest));
            })
            .flatMap(authResponse -> {
                var responseBody = new LoginRouteResponseBody(
                    authResponse.authenticationResult().idToken(),
                    authResponse.authenticationResult().accessToken()
                );

                return ServerResponse.ok()
                    .body(BodyInserters.fromValue(responseBody));
            });
    }



    private final CognitoIdentityProviderAsyncClient cognitoClient;
    private final String clientId;
}