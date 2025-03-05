package com.example.learningwebflux.notification.restapi.routes

import com.example.learningwebflux.notification.notification.NotificationRepository
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
internal class GetNotificationRouteHandler(private val repository: NotificationRepository) {
    fun handle(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable(request.pathVariable("id"))

        return repository.getById(id)
            .flatMap { notification -> ServerResponse.ok().body(BodyInserters.fromValue(notification)) }
            .switchIfEmpty(ServerResponse.notFound().build())
    }
}