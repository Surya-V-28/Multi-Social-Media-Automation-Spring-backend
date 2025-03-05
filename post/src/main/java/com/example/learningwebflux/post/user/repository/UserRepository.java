package com.example.learningwebflux.post.user.repository;

import com.example.learningwebflux.post.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<Void> save(User user);

    Mono<User> getWithId(String id);
}
