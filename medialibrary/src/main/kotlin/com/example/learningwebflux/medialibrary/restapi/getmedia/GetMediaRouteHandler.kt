package com.example.learningwebflux.medialibrary.restapi.getmedia

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
internal class GetMediaRouteHandler(
    private val mediaRepository: MediaRepository,
    private val s3Client: S3AsyncClient,
    private val s3Presigner: S3Presigner,
    @Value("\${aws.s3.bucket}") private val s3Bucket: String,
) {
    suspend fun handle(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        val media = mediaRepository.getWithId(id)!!

        val url = S3Utils.createPresignedUrl(s3Presigner, s3Bucket, id)

        val responseBody = MediaNetworkModel.fromDomainModel(media, url.toString())

        return ServerResponse.ok()
            .body(BodyInserters.fromValue(responseBody))
            .awaitSingle()
    }
}