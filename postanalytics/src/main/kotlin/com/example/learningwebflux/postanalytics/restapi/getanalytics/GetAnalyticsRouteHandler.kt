package com.example.learningwebflux.postanalytics.restapi.getanalytics

import com.example.learningwebflux.postanalytics.post.PostTargetType
import kotlinx.coroutines.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import java.time.OffsetDateTime
import kotlin.jvm.optionals.getOrNull

@Component
class GetAnalyticsRouteHandler(
    @Qualifier("postAnalyticsEntityTemplate") private val entityTemplate: R2dbcEntityTemplate,
) {
    suspend fun handle(request: ServerRequest): ServerResponse {
        val postTargetType: PostTargetType = PostTargetType.valueOf(request.pathVariable("postTargetType"))
        val postId = request.pathVariable("id")
        val metrics: List<String> = request.queryParams()["metrics"]!!

        val from: OffsetDateTime? = OffsetDateTime.parse(request.queryParam("from").getOrNull())
        val to: OffsetDateTime? = OffsetDateTime.parse(request.queryParam("to").getOrNull())

        val metricSeriesGroupNetworkModel = hashMapOf<String, MutableList<MetricSeriesDataPointNetworkModel>>()
        entityTemplate.databaseClient
            .sql("SELECT :metrics FROM :postTargetType WHERE id = :postId AND from = :from AND to = :to")
            .bind("metrics", metricsToSQLQuery(metrics))
            .bind("postTargetType", postTargetType)
            .bind("postId", postId)
            .bind("from", from!!)
            .bind("to", to!!)
            .map { row, _ -> row }
            .all()
            .asFlow()
            .collect { row ->
                val timestamp = OffsetDateTime.parse(row.get("timestamp") as String)

                val columnNames = row.metadata.columnMetadatas.map { e -> e.name }
                for (columnName in columnNames) {
                    if (nonMetricColumns.contains(columnName)) continue

                    if (!metricSeriesGroupNetworkModel.containsKey(columnName)) {
                        metricSeriesGroupNetworkModel[columnName] = mutableListOf()
                    }

                    val dataPoint = MetricSeriesDataPointNetworkModel(timestamp, row.get(columnName) as Double)
                    metricSeriesGroupNetworkModel[columnName]!!.add(dataPoint)
                }
            }


        val responseBody = GetAnalyticsResponseBody(data = metricSeriesGroupNetworkModel)
        return ServerResponse.ok()
            .body(BodyInserters.fromValue(responseBody))
            .awaitSingle()
    }

    private fun metricsToSQLQuery(metrics: List<String>): String {
        return if (metrics.isEmpty()) {
            "*"
        }
        else {
            metrics.reduceIndexed { index, accumulated, element ->
                var result = accumulated
                if (index != 0) result += ", "

                result += element

                return@reduceIndexed result
            }
        }
    }

    private fun addDateRangeToQuery(query: String, from: OffsetDateTime? = null, to: OffsetDateTime? = null): String {
        val conditions = mapOf<String, OffsetDateTime?>(
            Pair("from", from),
            Pair("to", to)
        )

        var result = query
        var conditionCount = 0
        for (condition in conditions.filterValues { value -> value != null }) {
            result += if (conditionCount != 0) {" AND"} else {""}
            if (condition.key == "from") {
                result += "${condition.key} >= ${condition.value.toString()}"
            }
            else if (condition.key == "to") {
                result += "${condition.key} < ${condition.value.toString()}"
            }
            conditionCount += 1
        }

        return result
    }


    companion object {
        val nonMetricColumns = listOf<String>( "id", "timestamp" )
    }
}

internal data class GetAnalyticsResponseBody(
    val data: Map<String, List<MetricSeriesDataPointNetworkModel>>,
)

internal data class MetricSeriesDataPointNetworkModel(
    val timestamp: OffsetDateTime,
    val value: Double,
)