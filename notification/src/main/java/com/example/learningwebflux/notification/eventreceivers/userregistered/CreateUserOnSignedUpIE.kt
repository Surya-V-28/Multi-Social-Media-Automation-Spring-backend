package com.example.learningwebflux.notification.eventreceivers.userregistered

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiver
import com.example.learningwebflux.common.integrationevents.UserSignedUpIE
import com.example.learningwebflux.notification.user.User
import com.example.learningwebflux.notification.user.UserRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
internal class CreateUserOnSignedUpIE(
    private val userRepository: UserRepository,
) : IntegrationEventReceiver<UserSignedUpIE> {
    override fun receive(aEvent: UserSignedUpIE): Mono<Void> {
        return userRepository.save(User(id = aEvent.id))
    }

}