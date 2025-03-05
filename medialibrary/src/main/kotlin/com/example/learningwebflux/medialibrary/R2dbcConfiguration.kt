package com.example.learningwebflux.medialibrary

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate

@Configuration
internal open class R2dbcConfiguration {
    @Bean
    open fun mediaLibraryEntityTemplate(): R2dbcEntityTemplate {
        val configuration = PostgresqlConnectionConfiguration.builder()
            .username("postgres")
            .host("localhost")
            .port(5432)
            .database("media_library")
            .build()

        return R2dbcEntityTemplate(PostgresqlConnectionFactory(configuration))
    }
}
