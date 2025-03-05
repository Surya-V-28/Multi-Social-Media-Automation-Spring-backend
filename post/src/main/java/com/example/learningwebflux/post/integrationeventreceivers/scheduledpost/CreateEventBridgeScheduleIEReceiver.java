package com.example.learningwebflux.post.integrationeventreceivers.scheduledpost;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiver;
import com.example.learningwebflux.common.integrationevents.PostScheduledIE;
import com.example.learningwebflux.post.scheduledpost.repository.ScheduledPostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.scheduler.SchedulerAsyncClient;
import software.amazon.awssdk.services.scheduler.model.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
@AllArgsConstructor
public class CreateEventBridgeScheduleIEReceiver implements IntegrationEventReceiver<PostScheduledIE> {
    @Override
    public Mono<Void> receive(PostScheduledIE event) {
        return Mono.empty();

//        return scheduledPostRepository.getWithId(event.id())
//            .flatMap(scheduledPost -> {
//                String targetInput;
//                try {
//                    targetInput = objectMapper.writeValueAsString(
//                        Map.of(
//                            "scheduledPostId", scheduledPost.id
//                        )
//                    );
//                }
//                catch (Exception exception) { return Mono.error(exception); }
//
//                String scheduledAt = scheduledPost.getScheduledTime()
//                    .atZoneSameInstant(ZoneId.of("UTC"))
//                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//
//                var createScheduleRequest = CreateScheduleRequest.builder()
//                    .name(String.format("scheduled_post_%s", scheduledPost.id))
//                    .flexibleTimeWindow( FlexibleTimeWindow.builder().mode(FlexibleTimeWindowMode.OFF).build() )
//                    .scheduleExpression(String.format("at(%s)", scheduledAt))
//                    .target(
//                        Target.builder()
//                            .roleArn("arn:aws:iam::471112730991:role/service-role/Amazon_EventBridge_Scheduler_EVENTBRIDGE_042f96464f")
//                            .arn("arn:aws:events:ap-south-1:471112730991:event-bus/default")
//                            .eventBridgeParameters(
//                                EventBridgeParameters.builder()
//                                    .source("backend_api")
//                                    .detailType("create_post")
//                                    .build()
//                            )
//                            .input(targetInput)
//                            .retryPolicy(RetryPolicy.builder().maximumRetryAttempts(0).build())
//                            .build()
//                    )
//                    .state(ScheduleState.ENABLED)
//                    .actionAfterCompletion(ActionAfterCompletion.DELETE)
//                    .build();
//
//                return Mono.fromFuture(schedulerClient.createSchedule(createScheduleRequest))
//                    .flatMap(eventBridgeResponse -> {
//                        scheduledPost.markAsScheduledOnEventBridge(eventBridgeResponse.scheduleArn());
//
//                        return scheduledPostRepository.save(scheduledPost);
//                    });
//            });
    }



    private final ScheduledPostRepository scheduledPostRepository;
    private final SchedulerAsyncClient schedulerClient;
    private ObjectMapper objectMapper;
}
