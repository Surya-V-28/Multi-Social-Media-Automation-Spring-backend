package com.example.learningwebflux.authentication.restapi.routes.signup;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventBus;
import com.example.learningwebflux.common.integrationevents.UserSignedUpIE;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;

@Component
public class SignUpRouteHandler {
    SignUpRouteHandler(
        CognitoIdentityProviderAsyncClient cognitoClient,
        @Value("${aws.cognito.clientId}") String clientId,
        @Value("${aws.cognito.userPoolId}") String userPoolId,
        IntegrationEventBus integrationEventBus
    ) {
        this.cognitoClient = cognitoClient;
        this.clientId = clientId;
        this.userPoolId = userPoolId;
        this.integrationEventBus = integrationEventBus;
    }

    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.bodyToMono(SignUpRouteRequestBody.class)
            .flatMap(requestBody -> {
                var signUpRequest = SignUpRequest.builder()
                    .clientId(clientId)
                    .username(requestBody.username())
                    .password(requestBody.password())
                    .userAttributes(AttributeType.builder().name("email").value(requestBody.email()).build())
                    .build();

                return Mono.fromFuture(cognitoClient.signUp(signUpRequest))
                    .flatMap(signUpResponse -> {
                        var confirmRequest = AdminConfirmSignUpRequest.builder()
                            .userPoolId(userPoolId)
                            .username(requestBody.username())
                            .build();

                        return Mono.fromFuture(cognitoClient.adminConfirmSignUp(confirmRequest))
                            .flatMap(confirmResponse -> {
                                integrationEventBus.publish(new UserSignedUpIE(signUpResponse.userSub()));

                                SignUpRouteResponseBody responseBody = new SignUpRouteResponseBody(signUpResponse.userSub());

                                return ServerResponse
                                    .ok()
                                    .body(BodyInserters.fromValue(responseBody));
                            });
                    });
            });
    }


    private final CognitoIdentityProviderAsyncClient cognitoClient;
    private final String clientId;
    private final String userPoolId;
    private final IntegrationEventBus integrationEventBus;
}