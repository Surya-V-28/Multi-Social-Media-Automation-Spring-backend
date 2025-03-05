package com.example.learningwebflux.post.platformconnection.repository;

import com.example.learningwebflux.post.platformconnection.PlatformConnection;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PlatformConnectionRepository {
    Mono<PlatformConnection> withId(String id);

    Mono<List<PlatformConnection>> ofUser(String userId);

    Mono<Void> save(PlatformConnection platformConnection);
}
