package com.example.learningwebflux.platformconnection.accesstokenrefresher;

import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

public interface AccessTokenRefresher {
    Mono<AccessTokenRefresher.RefreshAccessTokenResult> refresh(String refreshToken);

    public record RefreshAccessTokenResult(String accessToken, OffsetDateTime expiresAt) {}
}