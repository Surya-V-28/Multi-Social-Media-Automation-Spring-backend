package com.example.learningwebflux.notification

import com.fasterxml.jackson.databind.JsonNode
import com.example.learningwebflux.notification.notification.Notification
import com.example.learningwebflux.notification.notification.NotificationRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class NotificationApi internal constructor(private val notificationRepository: NotificationRepository) {
    fun createNotification(userId: String, message: String, type: String, details: JsonNode?): Mono<Void> {
        val notification = Notification.create(
            id = UUID.randomUUID().toString(),
            userId = userId,
            type = type,
            message = message,
            details = details,
        )

        return notificationRepository.save(notification)
            .then(Mono.fromRunnable {
                logger.info("Notification of type {} with details:\n{}", type, details?.toPrettyString())
            })
    }

    fun createNotificationAsync(userId: String, message: String, type: String, details: JsonNode?) {
        createNotification(userId, message, type, details).subscribe()
    }

    fun deleteNotification(id: String): Mono<Void> {
        return notificationRepository.delete(id)
    }


    public companion object {
        private val logger = LoggerFactory.getLogger(NotificationApi::class.java)!!
    }
}