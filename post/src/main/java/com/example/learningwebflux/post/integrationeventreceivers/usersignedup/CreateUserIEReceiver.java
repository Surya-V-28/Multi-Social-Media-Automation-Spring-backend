package com.example.learningwebflux.post.integrationeventreceivers.usersignedup;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiver;
import com.example.learningwebflux.common.integrationevents.UserSignedUpIE;
import com.example.learningwebflux.post.user.User;
import com.example.learningwebflux.post.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class CreateUserIEReceiver implements IntegrationEventReceiver<UserSignedUpIE> {
    @Override
    public Mono<Void> receive(UserSignedUpIE event) {
        var user = new User(event.id());

        return userRepository.save(user);
    }


    private final UserRepository userRepository;
}
