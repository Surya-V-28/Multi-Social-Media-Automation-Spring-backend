package com.example.learningwebflux.notification.notification.r2dbc

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.select
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
internal class R2dbcNotificationDataSource(@Qualifier("notificationEntityTemplate") entityTemplate: R2dbcEntityTemplate) {
    fun getWithId(id: String): Mono<NotificationDataModel> {
        return entityTemplate.select(NotificationDataModel::class.java)
            .matching(query(where("id").`is`(id)))
            .one()
    }

    fun getOfUser(userId: String): Mono<List<NotificationDataModel>> {
        return entityTemplate.select(NotificationDataModel::class.java)
            .matching(query(where("user_id").`is`(userId)))
            .all()
            .collectList()
    }

    fun save(dataModel: NotificationDataModel): Mono<Void> {
        return entityTemplate.select(NotificationDataModel::class.java)
            .matching(query(where("id").`is`(dataModel)))
            .one()
            .flatMap { _ -> entityTemplate.update(dataModel) }
            .switchIfEmpty(entityTemplate.insert(dataModel))
            .then()
    }

    private val entityTemplate: R2dbcEntityTemplate = entityTemplate
}