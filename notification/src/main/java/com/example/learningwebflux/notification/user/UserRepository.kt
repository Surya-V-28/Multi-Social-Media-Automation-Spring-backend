package com.example.learningwebflux.notification.user

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
internal class UserRepository(@Qualifier("notificationEntityTemplate") entityTemplate: R2dbcEntityTemplate) {
    fun save(user: User): Mono<Void> {
        return entityTemplate.select(User::class.java)
            .matching(query(where("id").`is`(user.id)))
            .one()
            .flatMap { _ -> entityTemplate.update(user) }
            .switchIfEmpty(entityTemplate.insert(user))
            .then()
    }

    fun getWithId(id: String): Mono<User> {
        return entityTemplate.select(User::class.java)
            .matching(query(where("id").`is`(id)))
            .one()
    }


    private val entityTemplate: R2dbcEntityTemplate = entityTemplate
}