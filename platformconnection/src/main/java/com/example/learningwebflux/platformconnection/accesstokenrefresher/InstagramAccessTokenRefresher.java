package com.example.learningwebflux.platformconnection.accesstokenrefresher;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Component
@AllArgsConstructor
public class InstagramAccessTokenRefresher implements AccessTokenRefresher {
    @Override
    public Mono<RefreshAccessTokenResult> refresh(String refreshToken) {
        var url = new DefaultUriBuilderFactory().builder()
            .scheme("https")
            .host("graph.instagram.com")
            .path("/refresh_access_token")
            .queryParam("grant_type", "ig_refresh_token")
            .queryParam("access_token", refreshToken)
            .build();

        return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(responseBody -> {
                return new RefreshAccessTokenResult(
                    responseBody.get("access_token").asText(),
                    OffsetDateTime.now().plusSeconds(responseBody.get("expires_in").asLong())
                );
            });
    }


    private final WebClient webClient;
}