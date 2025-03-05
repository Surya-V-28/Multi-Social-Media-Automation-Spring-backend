package com.example.learningwebflux.postanalytics.metricsfetcher

import com.example.learningwebflux.postanalytics.post.PostTargetType
import org.springframework.stereotype.Component

@Component
internal class MetricsFetcherRegistry(private val instagramFeedMetricsFetcher: InstagramFeedMetricsFetcher) {
    fun get(postTargetType: PostTargetType): MetricsFetcher {
        return fetchers[postTargetType]!!
    }

    private val fetchers = mapOf<PostTargetType, MetricsFetcher>(
        PostTargetType.InstagramFeed to instagramFeedMetricsFetcher,
    )
}