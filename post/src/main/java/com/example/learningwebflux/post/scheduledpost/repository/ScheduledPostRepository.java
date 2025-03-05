package com.example.learningwebflux.post.scheduledpost.repository;

import com.example.learningwebflux.post.scheduledpost.ScheduledPost;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ScheduledPostRepository {
    Mono<Void> save(ScheduledPost post);

    Mono<ScheduledPost> getWithId(String id);

    Mono<List<ScheduledPost>> getOfUser(String userId);

    Mono<Void> removeWithId(String id);
}