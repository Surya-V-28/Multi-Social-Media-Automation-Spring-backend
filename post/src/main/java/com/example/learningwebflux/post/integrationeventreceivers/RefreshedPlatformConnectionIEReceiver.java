package com.example.learningwebflux.post.integrationeventreceivers;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiver;
import com.example.learningwebflux.common.integrationevents.RefreshedPlatformConnectionIE;
import com.example.learningwebflux.post.platformconnection.repository.PlatformConnectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class RefreshedPlatformConnectionIEReceiver implements IntegrationEventReceiver<RefreshedPlatformConnectionIE> {
    @Override
    public Mono<Void> receive(RefreshedPlatformConnectionIE event) {
        return platformConnectionRepository.withId(event.id())
            .flatMap(platformConnection -> {
                platformConnection.accessToken = event.newAccessToken();

                return platformConnectionRepository.save(platformConnection);
            });
    }


    private final PlatformConnectionRepository platformConnectionRepository;
}
