package com.example.learningwebflux.postanalytics.platformconnection

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.awaitCount
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Component

@Component
internal class PlatformConnectionRepository(
    @Qualifier("postAnalyticsEntityTemplate") private val entityTemplate: R2dbcEntityTemplate,
) {
    suspend fun getWithId(id: String): PlatformConnection {
        return entityTemplate.select(PlatformConnection::class.java)
            .matching(query(where("id").`is`(id)))
            .one()
            .awaitSingle()
    }

    suspend fun save(platformConnection: PlatformConnection) {
        val count = entityTemplate.select(PlatformConnection::class.java)
            .matching(query(where("id").`is`(platformConnection.id)))
            .awaitCount()
            .toInt()

        if (count == 0) {
            entityTemplate.insert(platformConnection)
        }
        else {
            entityTemplate.update(platformConnection)
        }
    }
}