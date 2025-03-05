package com.example.learningwebflux.post.commands.getscheduledpost;

import com.example.learningwebflux.post.scheduledpost.ScheduledPost;
import com.example.learningwebflux.post.scheduledpost.repository.ScheduledPostRepository;
import reactor.core.publisher.Mono;

public class GetScheduledPostCommandHandler {
    public GetScheduledPostCommandHandler(ScheduledPostRepository scheduledPostRepository) {
        this.scheduledPostRepository = scheduledPostRepository;
    }

    public Mono<ScheduledPost> perform(String id) {
        return scheduledPostRepository.getWithId(id);
    }


    private final ScheduledPostRepository scheduledPostRepository;
}
