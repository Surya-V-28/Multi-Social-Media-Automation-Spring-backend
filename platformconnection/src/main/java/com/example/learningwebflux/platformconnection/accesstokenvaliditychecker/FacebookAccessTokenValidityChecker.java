package com.example.learningwebflux.platformconnection.accesstokenvaliditychecker;

import com.example.learningwebflux.platformconnection.Platform;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@AllArgsConstructor
public class FacebookAccessTokenValidityChecker implements AccessTokenValidityChecker {
    @Override
    public Mono<Boolean> check(String accessToken) {
        return webClient.get()
            .uri(URI.create(String.format("https://graph.facebook.com/me?access_token=%s", accessToken)))
            .exchangeToMono(response -> Mono.just(response.statusCode().value() > 200 && response.statusCode().value() < 300));
    }

    @Override
    public Platform getPlatform() { return Platform.facebook; }


    private final WebClient webClient;
}
