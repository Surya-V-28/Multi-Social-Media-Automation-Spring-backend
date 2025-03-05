package com.example.learningwebflux.platformconnection.commands.removeplatformconnection;

import com.example.learningwebflux.platformconnection.platformconnection.repository.PlatformConnectionRepository;
import com.example.learningwebflux.platformconnection.commands.common.exceptions.PlatformConnectionAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class RemovePlatformConnectionCommandHandler {
    public Mono<Void> perform(String userId, String id) {
        return platformConnectionRepository.exists(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new PlatformConnectionAlreadyExistsException(id));
                }

                return Mono.empty();
            })
            .then(platformConnectionRepository.remove(id));
    }



    private final PlatformConnectionRepository platformConnectionRepository;
}
