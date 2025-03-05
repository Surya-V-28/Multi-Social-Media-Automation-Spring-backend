package com.example.learningwebflux.authentication.restapi.routes.confirm;

import com.example.learningwebflux.authentication.utils.CognitoUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;

import java.security.InvalidKeyException;

@Component
public class ConfirmRouteHandler {
    public ConfirmRouteHandler(
        CognitoIdentityProviderAsyncClient cognitoClient,
        @Value("${aws.cognito.clientId}") String clientId,
        @Value("${aws.cognito.clientSecret}") String clientSecret
    ) {
        this.cognitoClient = cognitoClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.bodyToMono(ConfirmRouteRequestBody.class)
            .flatMap(requestBody -> {
                final String secretHash;
                try { secretHash = CognitoUtils.calculateSecretHash(requestBody.email(), clientId, clientSecret); }
                catch (InvalidKeyException exception) { return Mono.error(exception); }

                var confirmSignUpRequest = ConfirmSignUpRequest.builder()
                    .clientId(clientId)
                    .username(requestBody.email())
//                    .secretHash(secretHash)
                    .confirmationCode(requestBody.code())
                    .build();

                return Mono.fromFuture(cognitoClient.confirmSignUp(confirmSignUpRequest));
            })
            .flatMap(confirmSignUpResponse -> {
                if (confirmSignUpResponse.sdkHttpResponse().statusCode() == 400) {
                    return ServerResponse
                        .status(HttpStatusCode.valueOf(400))
                        .build();
                }

                return ServerResponse
                    .ok()
                    .build();
            });
    }



    private final CognitoIdentityProviderAsyncClient cognitoClient;

    private final String clientId;
    private final String clientSecret;
}
