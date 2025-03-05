package com.example.learningwebflux.medialibrary.restapi

import com.example.learningwebflux.medialibrary.restapi.deletemedia.DeleteMediaRouteHandler
import com.example.learningwebflux.medialibrary.restapi.addmedia.AddMediaRouteHandler
import com.example.learningwebflux.medialibrary.restapi.getallusersmedias.GetAllUsersMediasRouteHandler
import com.example.learningwebflux.medialibrary.restapi.getmedia.GetMediaRouteHandler
import com.example.learningwebflux.medialibrary.restapi.gets3uploadurl.GetS3UploadUrlRouteHandler
import kotlinx.coroutines.reactor.mono
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
internal open class RouterConfiguration {
    @Bean
    open fun mediaLibraryRouterFunctions(
        addMediaRouteHandler: AddMediaRouteHandler,
        getMediaRouteHandler: GetMediaRouteHandler,
        getAllUsersMediasRouteHandler: GetAllUsersMediasRouteHandler,
        deleteMediaRouteHandler: DeleteMediaRouteHandler,

        getUploadUrlRouteHandler: GetS3UploadUrlRouteHandler,
    ): RouterFunction<ServerResponse> {
        return RouterFunctions.nest(path("/api/media-library"),
            RouterFunctions
                .nest(path("/{userId}/media"),
                    RouterFunctions
                        .route(POST("")) { request -> mono { addMediaRouteHandler.handle(request) } }
                        .andRoute(GET("")) { request -> mono { getAllUsersMediasRouteHandler.handle(request) } }
                )
                .andNest(path("/{userId}/media/{id}"),
                    RouterFunctions
                        .route(GET("")) { request -> mono { getMediaRouteHandler.handle(request) } }
                        .andRoute(DELETE("")) { request -> mono { deleteMediaRouteHandler.handle(request) } }
                )
                .andRoute(POST("/s3/uploadUrl")) { request -> mono { getUploadUrlRouteHandler.handle(request) } }
        )
    }
}