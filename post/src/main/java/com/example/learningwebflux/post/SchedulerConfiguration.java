package com.example.learningwebflux.post;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.scheduler.SchedulerAsyncClient;

@Component
public class SchedulerConfiguration {
    @Bean
    public SchedulerAsyncClient schedulerAsyncClient() {
        return SchedulerAsyncClient.builder()
            .region(Region.AP_SOUTH_1)
            .build();
    }
}
