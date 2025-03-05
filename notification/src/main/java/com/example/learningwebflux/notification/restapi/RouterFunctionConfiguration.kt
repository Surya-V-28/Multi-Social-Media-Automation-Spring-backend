package com.example.learningwebflux.notification.restapi

import com.example.learningwebflux.notification.restapi.routes.GetNotificationRouteHandler
import com.example.learningwebflux.notification.restapi.routes.GetUsersNotificationsRouteHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.path
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
internal open class RouterFunctionConfiguration {
    @Bean
    open fun notificationRouterFunctions(
        getUsersNotificationsRouteHandler: GetUsersNotificationsRouteHandler,
        getNotificationRouteHandler: GetNotificationRouteHandler,
    ): RouterFunction<ServerResponse> {
        return RouterFunctions.nest(
            path("/api/notification"),
            RouterFunctions
                .route(GET("/{userId}/notifications"), getUsersNotificationsRouteHandler::handle)
                .andRoute(GET("/{userId}/notifications/{id}"), getNotificationRouteHandler::handle)
        )
    }
}