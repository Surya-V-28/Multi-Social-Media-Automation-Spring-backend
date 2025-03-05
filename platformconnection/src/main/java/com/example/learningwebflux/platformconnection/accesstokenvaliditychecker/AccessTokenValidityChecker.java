package com.example.learningwebflux.platformconnection.accesstokenvaliditychecker;

import com.example.learningwebflux.platformconnection.Platform;
import reactor.core.publisher.Mono;

public interface AccessTokenValidityChecker {
    Mono<Boolean> check(String accessToken);

    Platform getPlatform();
}
