package com.example.learningwebflux.postanalytics.metricsfetcher

import com.example.learningwebflux.postanalytics.platformconnection.PlatformConnection
import com.example.learningwebflux.postanalytics.post.Post
import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder

@Component
internal class InstagramFeedMetricsFetcher(private val webClient: WebClient) : MetricsFetcher {
    override suspend fun fetchMetrics(post: Post, platformConnection: PlatformConnection): Map<String, Double> {
        val metrics = hashMapOf<String, Double>()

        val uri = UriComponentsBuilder.fromHttpUrl("http://graph.facebook.com/v21.0/${post.createdPostId}/insights")
            .queryParam("metrics", "reach,likes,impressions")
            .queryParam("access_token", platformConnection.accessToken!!)
            .build()
            .toUri()

        val responseBody = webClient.get()
            .uri(uri)
            .retrieve()
            .bodyToMono(JsonNode::class.java)
            .awaitSingle()

        for (responseBodyMetric in responseBody.get("data").asIterable().toList()) {
            val metricName = responseBodyMetric.get("name").asText()
            val metricValue = responseBodyMetric.get("values").get(0).get("value").asDouble()
            metrics[metricName] = metricValue
        }

        return metrics
    }
}