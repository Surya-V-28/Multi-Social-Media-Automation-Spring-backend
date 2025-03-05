package com.example.learningwebflux.postanalytics.iereceivers.connectedtoplatform

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiver
import com.example.learningwebflux.common.integrationevents.ConnectedToPlatformIE
import com.example.learningwebflux.postanalytics.platformconnection.PlatformConnection
import com.example.learningwebflux.postanalytics.platformconnection.PlatformConnectionRepository
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
internal class CreatePlatformConnectionIEReceiver(private val platformConnectionRepository: PlatformConnectionRepository)
    : IntegrationEventReceiver<ConnectedToPlatformIE> {
    override fun receive(event: ConnectedToPlatformIE): Mono<Void> {
        val platformConnection = PlatformConnection(event.id, event.userId, event.platformUserId, event.accessToken)

        return mono { platformConnectionRepository.save(platformConnection) }
            .then()
    }

}