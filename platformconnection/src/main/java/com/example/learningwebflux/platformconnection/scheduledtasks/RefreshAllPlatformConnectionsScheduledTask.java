package com.example.learningwebflux.platformconnection.scheduledtasks;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventBus;
import com.example.learningwebflux.common.integrationevents.RefreshedPlatformConnectionIE;
import com.example.learningwebflux.platformconnection.accesstokenrefresher.AccessTokenRefresherRegistry;
import com.example.learningwebflux.platformconnection.accesstokenvaliditychecker.AccessTokenValidityCheckerRegistry;
import com.example.learningwebflux.platformconnection.platformconnection.repository.PlatformConnectionRepository;
import lombok.AllArgsConstructor;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class RefreshAllPlatformConnectionsScheduledTask implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        _refreshConnections();
    }

    public Mono<Void> _refreshConnections() {
        return platformConnectionRepository.getAllValid()
            .flatMapIterable(platformConnections -> platformConnections)
            .flatMap(platformConnection -> {
                var accessTokenRefresher = accessTokenRefresherRegistry.getItem(platformConnection.platform);

                var accessTokenValidityChecker = accessTokenValidityCheckerRegistry.get(platformConnection.platform);

                return accessTokenValidityChecker.check(platformConnection.getAccessToken())
                    .flatMap(isValidToken -> {
                        if (!isValidToken) {
                            platformConnection.invalidate();
                            return Mono.just(platformConnection);
                        }
                        else {
                            return accessTokenRefresher.refresh(platformConnection.getRefreshToken())
                                .map(refreshResult -> {
                                    var accessToken = refreshResult.accessToken();
                                    var expiresAt = refreshResult.expiresAt();
                                    platformConnection.setNewAccessToken(accessToken, expiresAt);

                                    return platformConnection;
                                });
                        }
                    })
                    .flatMap(platformConnectionRepository::save)
                    .then(Mono.fromRunnable(() -> {
                        var integrationEvent = new RefreshedPlatformConnectionIE(
                            platformConnection.id,
                            platformConnection.getAccessToken()
                        );

                        integrationEventBus.publish(integrationEvent);
                    }));
            })
            .then();
    }


    private final PlatformConnectionRepository platformConnectionRepository;
    private final AccessTokenRefresherRegistry accessTokenRefresherRegistry;
    private final AccessTokenValidityCheckerRegistry accessTokenValidityCheckerRegistry;
    private final IntegrationEventBus integrationEventBus;
}

@Component
@AllArgsConstructor
class JobScheduler {
    @EventListener(ApplicationReadyEvent.class)
    void scheduleJob() throws Exception {
        final var jobDetail = JobBuilder.newJob(RefreshAllPlatformConnectionsScheduledTask.class)
            .build();
        final var trigger = TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.cronSchedule("59 23 * * * ?"))
            .build();

        try { scheduler.scheduleJob(jobDetail, trigger); }
        catch (SchedulerException exception) {
            throw new Exception("Failed to schedule RefreshAllPlatformConnectionsScheduledTask");
        }
    }


    private final Scheduler scheduler;

    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);
}
