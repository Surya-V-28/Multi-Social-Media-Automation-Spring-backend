package com.example.learningwebflux.platformconnection.platformconnection.repository;

import com.example.learningwebflux.platformconnection.Platform;
import com.example.learningwebflux.platformconnection.platformconnection.PlatformConnection;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PlatformConnectionRepository {
    Mono<PlatformConnection> getWithId(String id);

    Mono<PlatformConnection> get(String userId, Platform platform, String platformUserId);

    Mono<List<PlatformConnection>> getRefreshables();

    Mono<List<PlatformConnection>> getAllValid();

    Mono<Boolean> exists(String id);

    Mono<Boolean> exists(String userId, Platform platform, String platformUserId);

    Mono<Void> save(PlatformConnection platformConnection);

    Mono<Void> remove(String id);
}
