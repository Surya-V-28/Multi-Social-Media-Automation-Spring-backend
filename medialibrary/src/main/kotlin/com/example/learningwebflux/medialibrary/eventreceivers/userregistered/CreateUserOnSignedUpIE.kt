package com.example.learningwebflux.medialibrary.eventreceivers.userregistered

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiver
import com.example.learningwebflux.common.integrationevents.UserSignedUpIE
import com.example.learningwebflux.medialibrary.user.User
import com.example.learningwebflux.medialibrary.user.UserRepository
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
internal class CreateUserOnSignedUpIE(
    private val userRepository: UserRepository,
) : IntegrationEventReceiver<UserSignedUpIE> {
    override fun receive(aEvent: UserSignedUpIE): Mono<Void> = mono {
        userRepository.save(User(id = aEvent.id))

        null
    }

}