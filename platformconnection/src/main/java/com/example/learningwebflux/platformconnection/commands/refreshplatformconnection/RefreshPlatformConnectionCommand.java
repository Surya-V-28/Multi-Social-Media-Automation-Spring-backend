package com.example.learningwebflux.platformconnection.commands.refreshplatformconnection;

import com.example.learningwebflux.platformconnection.accesstokenrefresher.AccessTokenRefresherRegistry;
import com.example.learningwebflux.platformconnection.commands.refreshplatformconnection.exceptions.RefreshTokenMissingException;
import com.example.learningwebflux.platformconnection.customer.repository.CustomerRepository;
import com.example.learningwebflux.platformconnection.platformconnection.repository.PlatformConnectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class RefreshPlatformConnectionCommand {
    public Mono<Void> perform(String userId, String id) {
        return platformConnectionRepository.getWithId(id)
            .flatMap(platformConnection -> {
                if (platformConnection.getRefreshToken() == null) {
                    return Mono.error(new RefreshTokenMissingException(platformConnection.id));
                }

                var accessTokenRefresher = accessTokenRefresherRegistry.getItem(platformConnection.platform);

                return accessTokenRefresher.refresh(platformConnection.getRefreshToken())
                    .flatMap(result -> {
                        platformConnection.setNewAccessToken(result.accessToken(), result.expiresAt());

                        return platformConnectionRepository.save(platformConnection);
                    });
            });
    }



    private final AccessTokenRefresherRegistry accessTokenRefresherRegistry;
    private final PlatformConnectionRepository platformConnectionRepository;
}