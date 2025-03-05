package com.example.learningwebflux.platformconnection.accesstokenrefresher;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Component
public class FacebookAccessTokenRefresher implements AccessTokenRefresher {
    public FacebookAccessTokenRefresher(
        WebClient webClient,
        @Value("${facebook.appId}") String facebookAppId,
        @Value("${facebook.appSecret}") String facebookAppSecret
    ) {
        this.webClient = webClient;
        this.facebookAppId = facebookAppId;
        this.facebookAppSecret = facebookAppSecret;
    }

    public Mono<AccessTokenRefresher.RefreshAccessTokenResult> refresh(String refreshToken) {
        var url = new DefaultUriBuilderFactory().builder()
            .scheme("https")
            .host("graph.facebook.com")
            .path("/v21.0/oauth/access_token")
            .queryParam("grant_type", "fb_exchange_token")
            .queryParam("client_id", facebookAppId)
            .queryParam("client_secret", facebookAppSecret)
            .queryParam("fb_exchange_token", refreshToken)
            .build();

        return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(responseBody -> {
                var expiresAt = (responseBody.has("expires_in")) ?
                    OffsetDateTime.now().plusSeconds(responseBody.get("expires_in").asLong()) :
                    OffsetDateTime.now().plusYears(20);

                return new AccessTokenRefresher.RefreshAccessTokenResult(
                    responseBody.get("access_token").asText(),
                    expiresAt
                );
            });
//            .retrieve()
//            .map(responseBody -> {
//                return new AccessTokenRefresher.RefreshAccessTokenResult(
//                    responseBody.get("access_token").asText(),
//                    OffsetDateTime.now().plusSeconds(responseBody.get("expires_in").asLong())
//                );
//            });
    }


    private final WebClient webClient;
    private final String facebookAppId;
    private final String facebookAppSecret;
}