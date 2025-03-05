package com.example.learningwebflux.medialibrary.restapi.addmedia

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventBus
import com.example.learningwebflux.common.integrationevents.medialibrarymediaadded.ImageMediaDetails
import com.example.learningwebflux.common.integrationevents.medialibrarymediaadded.MediaLibraryMediaAddedIE
import com.example.learningwebflux.common.integrationevents.medialibrarymediaadded.VideoMediaDetails
import com.example.learningwebflux.medialibrary.media.ImageMedia
import com.example.learningwebflux.medialibrary.media.Media
import com.example.learningwebflux.medialibrary.media.MediaInfo
import com.example.learningwebflux.medialibrary.media.VideoMedia
import com.example.learningwebflux.medialibrary.media.repository.MediaRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import kotlin.time.toJavaDuration

@Component
internal class AddMediaRouteHandler(
    private val mediaRepository: MediaRepository,
    private val integrationEventBus: IntegrationEventBus,
) {
    suspend fun handle(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId")

        val requestBody = request
            .bodyToMono(AddMediaRequestBody::class.java)
            .awaitSingle()

        val mediaInfo = MediaInfo(
            id = requestBody.mediaInfo.id,
            userId = userId,
            mimeType = requestBody.mediaInfo.mimeType,
            name = requestBody.mediaInfo.name,
            size = requestBody.mediaInfo.size,
        )

        val media = when (requestBody.mediaTypeDetails) {
            is AddMediaRequestBody.ImageMediaTypeDetails -> {
                ImageMedia(
                    mediaInfo = mediaInfo,
                    width = requestBody.mediaTypeDetails.width,
                    height = requestBody.mediaTypeDetails.height,
                )
            }

            is AddMediaRequestBody.VideoMediaTypeDetails -> {
                VideoMedia(
                    mediaInfo = mediaInfo,
                    duration = requestBody.mediaTypeDetails.duration,
                )
            }
        }

        mediaRepository.save(media)

        publishEvent(media)

        return ServerResponse.ok().build().awaitSingle()
    }

    private fun publishEvent(media: Media) {
        val event = MediaLibraryMediaAddedIE(
            media.mediaInfo.id,
            media.mediaInfo.userId,
            media.mediaInfo.name,
            media.mediaInfo.mimeType,
            media.mediaInfo.size,
            when (media) {
                is ImageMedia -> {
                    ImageMediaDetails(media.width, media.height)
                }

                is VideoMedia -> {
                    VideoMediaDetails(media.duration.toJavaDuration())
                }
            }
        )
        integrationEventBus.publish(event)
    }
}