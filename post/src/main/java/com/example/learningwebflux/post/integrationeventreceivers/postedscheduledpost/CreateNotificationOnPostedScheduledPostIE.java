package com.example.learningwebflux.post.integrationeventreceivers.postedscheduledpost;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiver;
import com.example.learningwebflux.common.integrationevents.PostedScheduledPostIE;
import com.example.learningwebflux.notification.NotificationApi;
import com.example.learningwebflux.post.scheduledpost.repository.ScheduledPostRepository;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class CreateNotificationOnPostedScheduledPostIE implements IntegrationEventReceiver<PostedScheduledPostIE> {
    @Override
    public Mono<Void> receive(PostedScheduledPostIE event) {
        return scheduledPostRepository.getWithId(event.id())
            .flatMap(scheduledPost -> {
                var details = JsonNodeFactory.instance.objectNode();
                details.put("id", event.id());

                return notificationApi.createNotification(
                    scheduledPost.userId,
                    "Scheduled Post posted",
                    "posted_scheduled_post",
                    details
                );
            });
    }


    private final NotificationApi notificationApi;
    private final ScheduledPostRepository scheduledPostRepository;
}
