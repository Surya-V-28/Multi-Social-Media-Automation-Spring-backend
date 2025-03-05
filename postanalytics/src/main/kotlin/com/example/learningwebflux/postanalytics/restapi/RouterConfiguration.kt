package com.example.learningwebflux.postanalytics.restapi

import com.example.learningwebflux.postanalytics.restapi.getanalytics.GetAnalyticsRouteHandler
import com.example.learningwebflux.postanalytics.restapi.sampleanalytics.SampleAnalyticsRouteHandler
import kotlinx.coroutines.reactor.mono
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.path
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
internal open class RouterConfiguration {
    @Bean
    open fun postAnalyticsRouterFunctions(
        getAnalyticsRouteHandler: GetAnalyticsRouteHandler,
        sampleAnalyticsRouteHandler: SampleAnalyticsRouteHandler,
    ): RouterFunction<ServerResponse> {
        return RouterFunctions
            .nest(path("/api/post-analytics"),
                RouterFunctions
                    .route(GET("{postTargetType}/{postId}")) { mono { getAnalyticsRouteHandler.handle(it) } }
                    .andNest(path("/testing"),
                        RouterFunctions.route(GET("/sample-instagram-data/{id}")) { mono { sampleAnalyticsRouteHandler.handle(it) } }
                    )
            )
    }
}