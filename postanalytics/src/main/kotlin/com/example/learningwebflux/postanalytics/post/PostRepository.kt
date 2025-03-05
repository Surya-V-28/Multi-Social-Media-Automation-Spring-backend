package com.example.learningwebflux.postanalytics.post

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
internal class PostRepository(
    @Qualifier("postAnalyticsEntityTemplate") private val entityTemplate: R2dbcEntityTemplate
) {
    suspend fun getAllWithinMonth(): List<Post> {
         return entityTemplate.select(Post::class.java)
             .matching(query(where("created_at").greaterThan(OffsetDateTime.now().minusMonths(1))))
             .all()
             .collectList()
             .awaitSingle()
    }
}