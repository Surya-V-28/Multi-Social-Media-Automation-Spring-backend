package com.example.learningwebflux.platformconnection.commands.connecttoplatform;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventBus;
import com.example.learningwebflux.common.integrationevents.ConnectedToPlatformIE;
import com.example.learningwebflux.platformconnection.platformconnection.repository.PlatformConnectionRepository;
import com.example.learningwebflux.platformconnection.Platform;
import com.example.learningwebflux.platformconnection.platformconnection.PlatformConnection;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ConnectToPlatformCommandHandler {
    public Mono<String> perform(
        String userId,
        Platform platform,
        String accessToken,
        String refreshToken,
        OffsetDateTime expiresAt
    ) {
        return getPlatformUserId(platform, accessToken)
            .flatMap(platformUserId -> {
                return platformConnectionRepository.get(userId, platform, platformUserId)
                    .switchIfEmpty(
                        Mono.just(
                            new PlatformConnection(
                                UUID.randomUUID().toString(),
                                userId,
                                platform,
                                platformUserId,
                                refreshToken,
                                accessToken,
                                expiresAt
                            )
                        )
                    );
            })
            .flatMap(platformConnection -> {
                return platformConnectionRepository.save(platformConnection)
                    .then(Mono.defer(() -> {
                        var integrationEvent = new ConnectedToPlatformIE(
                            platformConnection.id,
                            platformConnection.userId,
                            platformConnection.platform.name(),
                            platformConnection.platformUserId,
                            platformConnection.getAccessToken()
                        );
                        integrationEventBus.publish(integrationEvent);

                        return Mono.just(platformConnection.id);
                    }));
            });
    }

    private Mono<String> getPlatformUserId(Platform platform, String accessToken) {
        if (platform == Platform.facebook) {
            return webClient.get()
                .uri(URI.create("https://graph.facebook.com/v21.0/me?access_token=" + accessToken))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(responseBody -> responseBody.get("id").asText());
        }
        else if (platform == Platform.instagram) {
            return webClient.get()
                .uri(URI.create(String.format("https://graph.instagram.com/v21.0/me?access_token=%s", accessToken)))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(responseBody -> responseBody.get("id").asText());
        }
        else {
            var exception = new Exception(String.format("Function getPlatformUserId does not handle platform %s", platform.name()));
            return Mono.error(exception);
        }
    }




    private WebClient webClient;
    private final PlatformConnectionRepository platformConnectionRepository;
    private final IntegrationEventBus integrationEventBus;
}
