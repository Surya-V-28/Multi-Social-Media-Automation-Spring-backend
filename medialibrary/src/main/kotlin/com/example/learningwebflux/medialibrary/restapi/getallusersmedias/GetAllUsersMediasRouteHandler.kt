package com.example.learningwebflux.medialibrary.restapi.getallusersmedias

import com.example.learningwebflux.medialibrary.media.repository.MediaRepository
import com.example.learningwebflux.medialibrary.restapi.models.MediaNetworkModel
import com.example.learningwebflux.medialibrary.utils.S3Utils
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Component
internal class GetAllUsersMediasRouteHandler(
    private val mediaRepository: MediaRepository,
    private val s3Presigner: S3Presigner,
    @Value("\${aws.s3.bucket}") private val s3Bucket: String,
) {
    suspend fun handle(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId")
        val medias = mediaRepository.getOfUser(userId)

        val mediaNetworkModels = mutableListOf<MediaNetworkModel>()
        for (media in medias) {
            val url = S3Utils.createPresignedUrl(s3Presigner, s3Bucket, media.mediaInfo.id)
            val mediaNetworkModel = MediaNetworkModel.fromDomainModel(media, url.toString())
            mediaNetworkModels.add(mediaNetworkModel)
        }

        val responseBody = ResponseBody(data = mediaNetworkModels)
        return ServerResponse.ok()
            .body(BodyInserters.fromValue(responseBody))
            .awaitSingle()
    }
}

private data class ResponseBody(
    val data: List<MediaNetworkModel>,
)