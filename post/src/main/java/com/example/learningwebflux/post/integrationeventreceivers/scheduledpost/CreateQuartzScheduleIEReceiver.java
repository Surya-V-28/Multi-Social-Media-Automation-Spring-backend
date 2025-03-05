package com.example.learningwebflux.post.integrationeventreceivers.scheduledpost;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventBus;
import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiver;
import com.example.learningwebflux.common.integrationevents.PostScheduledIE;
import com.example.learningwebflux.platformconnection.PlatformConnectionApi;
import com.example.learningwebflux.post.commands.fulfillscheduledpost.FulfillScheduledPostCommandHandler;
import com.example.learningwebflux.post.integrationevents.ScheduledPostPrePostCheckCompletedIE;
import com.example.learningwebflux.post.platformconnection.PlatformConnection;
import com.example.learningwebflux.post.platformconnection.repository.PlatformConnectionRepository;
import com.example.learningwebflux.post.scheduledpost.ScheduledPost;
import com.example.learningwebflux.post.scheduledpost.repository.ScheduledPostRepository;
import lombok.AllArgsConstructor;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CreateQuartzScheduleIEReceiver implements IntegrationEventReceiver<PostScheduledIE> {
    @Override
    public Mono<Void> receive(PostScheduledIE postScheduledIE) {
        logger.info(String.format("Scheduling scheduled post %s", postScheduledIE.id()));

        return scheduledPostRepository.getWithId(postScheduledIE.id())
            .flatMap(scheduledPost -> {
                var jobDetail = JobBuilder.newJob(FulfillPostScheduleJob.class)
                    .usingJobData("scheduledPostId", scheduledPost.id)
                    .withIdentity(scheduledPost.id, "scheduled_posts")
                    .build();
                var trigger = TriggerBuilder.newTrigger()
                    .startAt(Date.from(scheduledPost.getScheduledTime().toInstant()))
                    .build();

                try { scheduler.scheduleJob(jobDetail, trigger); }
                catch (SchedulerException exception) {
                    return Mono.error(exception);
                }

                scheduledPost.markScheduled(scheduledPost.id);

                return scheduledPostRepository.save(scheduledPost);
            });
    }



    private final Scheduler scheduler;
    private final ScheduledPostRepository scheduledPostRepository;

    private static final Logger logger = LoggerFactory.getLogger(CreateQuartzScheduleIEReceiver.class);
}

@Component
@AllArgsConstructor
class FulfillPostScheduleJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var scheduledPostId = context.getJobDetail().getJobDataMap().getString("scheduledPostId");
        commandHandler.perform(scheduledPostId).subscribe();
    }


    private final FulfillScheduledPostCommandHandler commandHandler;
}

@Component
@AllArgsConstructor
class PrePostCheckJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var scheduledPostId = context.getJobDetail().getJobDataMap().getString("scheduledPostId");

        scheduledPostRepository.getWithId(scheduledPostId)
            .flatMap(scheduledPost -> {
                var connectionIds = scheduledPost
                    .getTargets()
                    .stream()
                    .map(e -> e.platformConnectionId)
                    .toList();

                return platformConnectionApi.checkConnectionsValidity(connectionIds)
                    .flatMap(validityMap -> {
                        var allValid = !validityMap.containsValue(false);
                        eventBus.publish(new ScheduledPostPrePostCheckCompletedIE(scheduledPost.id, allValid));

                        return Mono.empty();
                    });
            })
            .subscribe();
    }


    private final ScheduledPostRepository scheduledPostRepository;
    private final PlatformConnectionApi platformConnectionApi;
    private final IntegrationEventBus eventBus;
}