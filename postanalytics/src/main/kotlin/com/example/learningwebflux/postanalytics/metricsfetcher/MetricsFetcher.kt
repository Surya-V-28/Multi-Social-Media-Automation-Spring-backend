package com.example.learningwebflux.postanalytics.metricsfetcher

import com.example.learningwebflux.postanalytics.platformconnection.PlatformConnection
import com.example.learningwebflux.postanalytics.post.Post

internal interface MetricsFetcher {
    suspend fun fetchMetrics(post: Post, platformConnection: PlatformConnection): Map<String, Double>
}