package com.example.learningwebflux.postanalytics

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate

@Configuration
open class R2dbcConfiguration {
    @Bean
    open fun postAnalyticsEntityTemplate(): R2dbcEntityTemplate {
        val configuration = PostgresqlConnectionConfiguration.builder()
            .username("postgres")
            .host("localhost")
            .port(5432)
            .database("post_analytics")
            .build()

        return R2dbcEntityTemplate(PostgresqlConnectionFactory(configuration))
    }
}