package com.example.learningwebflux.post.integrationeventreceivers.scheduledpostprecheckcompleted;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiver;
import com.example.learningwebflux.post.integrationevents.ScheduledPostPrePostCheckCompletedIE;
import com.example.learningwebflux.post.scheduledpost.repository.ScheduledPostRepository;
import com.example.learningwebflux.notification.NotificationApi;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class NotifyPreCheckFailureIEReceiver implements IntegrationEventReceiver<ScheduledPostPrePostCheckCompletedIE> {
    @Override
    public Mono<Void> receive(ScheduledPostPrePostCheckCompletedIE event) {
        if (event.didSucceed()) return Mono.empty();

        return scheduledPostRepository.getWithId(event.id())
            .flatMap(scheduledPost -> {
                ObjectNode details = JsonNodeFactory.instance.objectNode();
                details.put("id", scheduledPost.id);

                return notificationApi.createNotification(
                    scheduledPost.userId,
                    "Scheduled post Pre-Post Check failed, we will be unable to create your post",
                    "scheduled_post_pre_post_check_failed",
                    details
                );
            });
    }


    private final ScheduledPostRepository scheduledPostRepository;
    private final NotificationApi notificationApi;
}