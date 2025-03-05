package com.example.learningwebflux.postanalytics.restapi.sampleanalytics

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Component
internal class SampleAnalyticsRouteHandler(
    @Qualifier("postAnalyticsEntityTemplate") private val entityTemplate: R2dbcEntityTemplate,
) {
    suspend fun handle(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")

        val row = entityTemplate.databaseClient
            .sql("SELECT * FROM sample_instagram_feed_data WHERE id = $1 and timestamp between $2 and $3")
            .bind("$1", id)
            .bind("$2", OffsetDateTime.of(LocalDate.now(), LocalTime.MIN, ZoneOffset.of("+05:30")))
            .bind("$3", OffsetDateTime.of(LocalDate.now(), LocalTime.MAX, ZoneOffset.of("+05:30")))
            .fetch()
            .awaitSingle()

        val responseBody = ResponseBody(
            data = metrics.map { e -> ResponseBody.Metric(e, listOf(ResponseBody.MetricDataPoint(row[e] as Long))) }
        )
        return ServerResponse.ok()
            .body(BodyInserters.fromValue(responseBody))
            .awaitSingle()
    }


    private val metrics: List<String> = listOf("likes", "comments", "impressions")
}

internal data class ResponseBody(
    val data: List<Metric>,
) {
    data class Metric(
        val name: String,
        val values: List<MetricDataPoint>,
    )

    data class MetricDataPoint(
        val value: Long,
    )
}