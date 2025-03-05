package com.example.learningwebflux.platformconnection;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

@Configuration
public class R2dbcConfiguration {
    @Bean
    public R2dbcEntityTemplate platformConnectionEntityTemplate() {
        var configuration = PostgresqlConnectionConfiguration.builder()
            .username("postgres")
            .host("localhost")
            .port(5432)
            .database("platform_connection")
            .build();

        return new R2dbcEntityTemplate(new PostgresqlConnectionFactory(configuration));
    }
}
