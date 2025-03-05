package com.example.learningwebflux.medialibrary.user

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Component

@Component
internal class UserRepository(
    @Qualifier("mediaLibraryEntityTemplate") private val entityTemplate: R2dbcEntityTemplate,
) {
    suspend fun get(id: String): User? {
        return entityTemplate.select(User::class.java)
            .matching(query( where("id").`is`(id) ))
            .one()
            .awaitSingleOrNull()
    }

    suspend fun save(user: User) {
        val count = entityTemplate.select(User::class.java)
            .matching(query( where("id").`is`(user.id) ))
            .count()
            .awaitSingle()

        if (count == 0L) {
            entityTemplate.insert(user).awaitSingle()
        }
        else {
            entityTemplate.update(user).awaitSingle()
        }
    }
}