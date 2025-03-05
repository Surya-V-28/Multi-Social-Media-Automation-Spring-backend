package com.example.learningwebflux.post;

import com.example.learningwebflux.common.integrationeventbus.ModuleIntegrationReceiversRegistry;
import com.example.learningwebflux.platformconnection.platformconnection.repository.PlatformConnectionRepository;
import com.example.learningwebflux.post.integrationeventreceivers.connectedtoplatform.CreatePlatformConnectionIEReceiver;
import com.example.learningwebflux.post.integrationeventreceivers.medialibrarymediaitemadded.AddMediaOnMediaItemAddedIEReceiver;
import com.example.learningwebflux.post.integrationeventreceivers.postedscheduledpost.CreateNotificationOnPostedScheduledPostIE;
import com.example.learningwebflux.post.integrationeventreceivers.scheduledpost.CreateQuartzScheduleIEReceiver;
import com.example.learningwebflux.post.integrationeventreceivers.scheduledpostprecheckcompleted.NotifyPreCheckFailureIEReceiver;
import com.example.learningwebflux.post.integrationeventreceivers.usersignedup.CreateUserIEReceiver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class IntegrationEventReceiversConfiguration {
    @Bean
    public ModuleIntegrationReceiversRegistry postIntegrationReceiversRegistry(PlatformConnectionRepository platformConnectionRepository) {
        return new ModuleIntegrationReceiversRegistry(
            List.of(
                CreatePlatformConnectionIEReceiver.class,
                CreateUserIEReceiver.class,
//                CreateEventBridgeScheduleIEReceiver.class,
                CreateQuartzScheduleIEReceiver.class,
                NotifyPreCheckFailureIEReceiver.class,
                CreateNotificationOnPostedScheduledPostIE.class,
                AddMediaOnMediaItemAddedIEReceiver.class
            )
        );
    }
}
