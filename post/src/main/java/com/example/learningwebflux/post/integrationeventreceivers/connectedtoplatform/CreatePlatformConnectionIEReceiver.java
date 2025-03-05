package com.example.learningwebflux.post.integrationeventreceivers.connectedtoplatform;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiver;
import com.example.learningwebflux.common.integrationevents.ConnectedToPlatformIE;
import com.example.learningwebflux.post.Platform;
import com.example.learningwebflux.post.platformconnection.PlatformConnection;
import com.example.learningwebflux.post.platformconnection.repository.PlatformConnectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class CreatePlatformConnectionIEReceiver implements IntegrationEventReceiver<ConnectedToPlatformIE> {
    @Override
    public Mono<Void> receive(ConnectedToPlatformIE event) {
        var platformConnection = new PlatformConnection(
            event.id(),
            event.userId(),
            Platform.valueOf(event.platform()),
            event.platformUserId(),
            event.accessToken()
        );

        return platformConnectionRepository.save(platformConnection);
    }


    private final PlatformConnectionRepository platformConnectionRepository;
}