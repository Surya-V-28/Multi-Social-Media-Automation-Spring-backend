package com.example.learningwebflux.postanalytics.user

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.r2dbc.core.*
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Component

@Component
internal class UserRepository(
    @Qualifier("postAnalyticsEntityTemplate") private val entityTemplate: R2dbcEntityTemplate,
) {
    suspend fun save(user: User) {
        val count = entityTemplate.select(User::class.java)
            .matching(query(where("id").`is`(user.id)))
            .awaitCount()
            .toInt()

        if (count == 0) {
            entityTemplate.insert(user)
        }
        else {
            entityTemplate.update(user)
        }
    }

    suspend fun getWithId(id: String): User? {
        return entityTemplate.select(User::class.java)
            .matching(query( where("id").`is`(id) ))
            .awaitOneOrNull()
    }
}