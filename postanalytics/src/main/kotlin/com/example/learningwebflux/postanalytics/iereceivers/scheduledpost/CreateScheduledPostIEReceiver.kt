package com.example.learningwebflux.postanalytics.iereceivers.scheduledpost

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiver
import com.example.learningwebflux.common.integrationevents.PostScheduledIE
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
internal class CreateScheduledPostIEReceiver : IntegrationEventReceiver<PostScheduledIE> {
    override fun receive(event: PostScheduledIE): Mono<Void> {
        return Mono.empty()
    }
}