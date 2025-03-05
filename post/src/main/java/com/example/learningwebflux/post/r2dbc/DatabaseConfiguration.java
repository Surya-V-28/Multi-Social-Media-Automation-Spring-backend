package com.example.learningwebflux.post.r2dbc;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

@Configuration
public class DatabaseConfiguration {
    @Bean
    public R2dbcEntityTemplate postEntityTemplate() {
        var configuration = PostgresqlConnectionConfiguration.builder()
            .username("postgres")
            .host("localhost")
            .port(5432)
            .database("post")
            .build();

        return new R2dbcEntityTemplate(new PostgresqlConnectionFactory(configuration));
    }
}
