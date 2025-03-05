package com.example.learningwebflux.postanalytics.iereceivers.usersignedup

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiver
import com.example.learningwebflux.common.integrationevents.UserSignedUpIE
import com.example.learningwebflux.postanalytics.user.User
import com.example.learningwebflux.postanalytics.user.UserRepository
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
internal class CreateUserIEReceiver(private val userRepository: UserRepository) : IntegrationEventReceiver<UserSignedUpIE> {
    override fun receive(event: UserSignedUpIE): Mono<Void> {
        return mono { userRepository.save(User(event.id)) }
            .then()
    }
}