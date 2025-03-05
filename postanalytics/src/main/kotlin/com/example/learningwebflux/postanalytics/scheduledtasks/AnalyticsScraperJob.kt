package com.example.learningwebflux.postanalytics.scheduledtasks

import com.example.learningwebflux.postanalytics.platformconnection.PlatformConnectionRepository
import com.example.learningwebflux.postanalytics.post.PostRepository
import com.example.learningwebflux.postanalytics.metricsfetcher.MetricsFetcherRegistry
import kotlinx.coroutines.*
import org.quartz.CronScheduleBuilder
import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.JobExecutionContext
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
internal class AnalyticsScraperJob(
    private val platformConnectionRepository: PlatformConnectionRepository,
    private val postRepository: PostRepository,
    private val metricsFetcherRegistry: MetricsFetcherRegistry,
    @Qualifier("postAnalyticsEntityTemplate") private val entityTemplate: R2dbcEntityTemplate,
) : Job {
    override fun execute(context: JobExecutionContext) {
         runBlocking(Dispatchers.IO) {
            val posts = postRepository.getAllWithinMonth()

            val jobs = mutableListOf<kotlinx.coroutines.Job>()
            for (post in posts) {
                val job = CoroutineScope(currentCoroutineContext()).launch {
                    val platformConnection = platformConnectionRepository.getWithId(post.platformConnectionId)

                    val metricsFetcher = metricsFetcherRegistry.get(post.type)
                    val fetchedMetrics: Map<String, Double> = metricsFetcher.fetchMetrics(post, platformConnection)

                    val queryColumnNames = sqlizeMetrics(listOf("id", "timestamp") + fetchedMetrics.keys.toList())
                    val queryValues = sqlizeMetrics(
                        listOf(post.createdPostId, OffsetDateTime.now().toString())
                            + fetchedMetrics.values.map { e -> e.toString() }
                    )
                    val query = "INSERT INTO $1 VALUES $queryColumnNames VALUES $queryValues"
                    entityTemplate.databaseClient.sql(query)
                        .bind("$1", post.type.name)
                        .fetch()
                        .awaitRowsUpdated()
                }

                jobs.add(job)
            }

             jobs.forEach { e -> e.join() }
        }
    }

    private fun sqlizeMetrics(values: List<String>): String {
        val result = StringBuilder("(")

        for (metric in values.withIndex()) {
            if (metric.index != 0) result.append(",")

            result.append(metric.value)
        }

        result.append(")")

        return result.toString()
    }
}

@Component
internal class JobScheduler(private val scheduler: Scheduler) {
    @EventListener(ApplicationReadyEvent::class)
    fun scheduleJob() {
        val trigger = TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.cronSchedule("59 23 * * * ?"))
            .build()

        val job = JobBuilder.newJob(AnalyticsScraperJob::class.java).build()

        scheduler.scheduleJob(job, trigger)
    }
}