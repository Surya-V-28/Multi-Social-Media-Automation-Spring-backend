package com.example.learningwebflux.notification.notification.r2dbc

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import com.example.learningwebflux.notification.notification.Notification
import com.example.learningwebflux.notification.notification.NotificationRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Primary
@Component
internal class R2dbcNotificationRepository(
    private val dataSource: R2dbcNotificationDataSource,
    @Qualifier("notificationEntityTemplate") private val entityTemplate: R2dbcEntityTemplate,
    private val objectMapper: ObjectMapper,
) : NotificationRepository {
    override fun save(domainModel: Notification): Mono<Void> {
        return dataSource.save(mapDomainToDataModel(domainModel))
    }

    override fun getById(id: String): Mono<Notification> {
        return dataSource.getWithId(id)
            .map { dataModel -> mapDataToDomainModel(dataModel) }
    }

    override fun getOfUser(userId: String): Mono<List<Notification>> {
        return dataSource.getOfUser(userId)
            .flatMapIterable { dataModels -> dataModels }
            .map { dataModel -> mapDataToDomainModel(dataModel) }
            .collectList()

    }

    override fun delete(id: String): Mono<Void> {
        return entityTemplate.delete(NotificationDataModel::class.java)
            .matching(query(where("id").`is`(id)))
            .all()
            .then()
    }

    private fun mapDataToDomainModel(dataModel: NotificationDataModel): Notification {
        return Notification(
            id = dataModel.id,
            userId = dataModel.userId,
            type = dataModel.type,
            createdAt = dataModel.createdAt,
            message = dataModel.message,
            details = if (dataModel.details != null) {
                objectMapper.readValue(dataModel.details.asString(), JsonNode::class.java)
            }
            else {
                null
            },
            read = true,
        )
    }

    private fun mapDomainToDataModel(domainModel: Notification): NotificationDataModel {
        return NotificationDataModel(
            id = domainModel.id,
            userId = domainModel.userId,
            type = domainModel.type,
            message = domainModel.message,
            createdAt = domainModel.createdAt,
            details = if (domainModel.details != null) Json.of(domainModel.details.toString()) else null,
            read = domainModel.read,
        )
    }
}