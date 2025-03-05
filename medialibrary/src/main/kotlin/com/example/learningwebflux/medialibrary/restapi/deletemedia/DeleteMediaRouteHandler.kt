package com.example.learningwebflux.medialibrary.restapi.deletemedia

import com.example.learningwebflux.medialibrary.media.repository.MediaRepository
import com.example.learningwebflux.medialibrary.restapi.models.MediaNetworkModel
import com.example.learningwebflux.medialibrary.utils.S3Utils
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest

@Component
internal class DeleteMediaRouteHandler(
    private val mediaRepository: MediaRepository,
    private val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket}") private val s3Bucket: String,
) {
    suspend fun handle(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")

        mediaRepository.delete(id)
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(s3Bucket).key(id).build())
            .await()


        return ServerResponse.ok().build().awaitSingle()
    }
}