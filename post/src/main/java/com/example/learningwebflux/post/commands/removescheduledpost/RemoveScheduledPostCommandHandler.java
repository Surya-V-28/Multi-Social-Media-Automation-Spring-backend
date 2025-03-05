package com.example.learningwebflux.post.commands.removescheduledpost;

import com.example.learningwebflux.post.scheduledpost.repository.ScheduledPostRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.scheduler.SchedulerAsyncClient;
import software.amazon.awssdk.services.scheduler.model.DeleteScheduleRequest;

@Configuration
@AllArgsConstructor
public class RemoveScheduledPostCommandHandler {
    public Mono<Void> perform(String userId, String id) {
        return scheduledPostRepository.getWithId(id)
            .flatMap(scheduledPost -> {
                var deleteScheduleRequest = DeleteScheduleRequest.builder()
                    .name(scheduledPost.getEventBridgeId())
                    .build();

                return Mono.fromFuture(client.deleteSchedule(deleteScheduleRequest))
                    .then(scheduledPostRepository.removeWithId(scheduledPost.id));
            });
    }


    private final ScheduledPostRepository scheduledPostRepository;
    private final SchedulerAsyncClient client;
}
