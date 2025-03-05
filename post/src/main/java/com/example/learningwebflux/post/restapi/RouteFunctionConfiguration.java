package com.example.learningwebflux.post.restapi;

import com.example.learningwebflux.post.restapi.routes.fulfillscheduledpost.FulfillScheduledPostRouteHandler;
import com.example.learningwebflux.post.restapi.routes.gets3uploadurl.GetS3UploadUrlRouteHandler;
import com.example.learningwebflux.post.restapi.routes.getscheduledpost.GetScheduledPostRouteHandler;
import com.example.learningwebflux.post.restapi.routes.getscheduledposts.GetUsersScheduledPostsRouteHandler;
import com.example.learningwebflux.post.restapi.routes.removescheduledpost.RemoveScheduledPostRouteHandler;
import com.example.learningwebflux.post.restapi.routes.schedulepost.SchedulePostRouteHandler;
import com.example.learningwebflux.post.restapi.routes.validatepost.ValidatePostRouteHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouteFunctionConfiguration {
    @Bean
    public RouterFunction<ServerResponse> postRouterFunctions(
        SchedulePostRouteHandler schedulePostRouteHandler,
        GetUsersScheduledPostsRouteHandler getUsersScheduledPostsRouteHandler,
        GetScheduledPostRouteHandler getScheduledPostRouteHandler,
        FulfillScheduledPostRouteHandler fulfillScheduledPostRouteHandler,
        RemoveScheduledPostRouteHandler removeScheduledPostRouteHandler,
        ValidatePostRouteHandler validatePostRouteHandler,

        GetS3UploadUrlRouteHandler getS3UploadUrlRouteHandler
    ) {
        return RouterFunctions
            .nest(
                path("/api/post"),
                RouterFunctions
                    .nest(
                        path("/{userId}/posts"),
                        RouterFunctions
                            .route(POST(""), schedulePostRouteHandler::handle)
                            .andRoute(GET(""), getUsersScheduledPostsRouteHandler::handle)
                            .andNest(
                                path("/{id}"),
                                RouterFunctions
                                    .route(GET(""), getScheduledPostRouteHandler::handle)
                                    .andRoute(DELETE(""), removeScheduledPostRouteHandler::handle)
                            )
                    )
                    .andRoute(POST("/posts/fulfill"), fulfillScheduledPostRouteHandler::handle)
                    .andRoute(PUT("/posts/validate"), validatePostRouteHandler::handle)
                    .andNest(
                        path("/s3"),
                        RouterFunctions
                            .route(POST(""), getS3UploadUrlRouteHandler::handle)
                    )
            );
    }
}
