package com.example.learningwebflux.notification.restapi.routes

import com.example.learningwebflux.notification.notification.Notification
import com.example.learningwebflux.notification.notification.NotificationRepository
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
internal class GetUsersNotificationsRouteHandler(private val notificationRepository: NotificationRepository,) {
    fun handle(request: ServerRequest): Mono<ServerResponse> {
        return notificationRepository.getOfUser(request.pathVariable("userId"))
            .flatMap { notifications ->
                val responseBody = ResponseBody(data = notifications)

                return@flatMap ServerResponse.ok()
                    .body(BodyInserters.fromValue(responseBody))
            }
    }
}

private data class ResponseBody(
    val data: List<Notification>,
)