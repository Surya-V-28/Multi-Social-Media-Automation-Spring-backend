package com.example.learningwebflux.notification.notification

import reactor.core.publisher.Mono

internal interface NotificationRepository {
    fun save(domainModel: Notification): Mono<Void>

    fun getById(id: String): Mono<Notification>

    fun getOfUser(userId: String): Mono<List<Notification>>

    fun delete(id: String): Mono<Void>
}