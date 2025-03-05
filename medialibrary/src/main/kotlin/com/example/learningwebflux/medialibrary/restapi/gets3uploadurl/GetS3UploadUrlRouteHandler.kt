package com.example.learningwebflux.medialibrary.restapi.gets3uploadurl

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.MimeType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URL
import java.time.Duration
import java.util.*

@Component
internal class GetS3UploadUrlRouteHandler(
    private val s3Presigner: S3Presigner,
    @Value("\${aws.s3.bucket}") private val bucketName: String,
) {
    suspend fun handle(request: ServerRequest): ServerResponse {
        val requestBody = request.bodyToMono(RequestBody::class.java).awaitSingle()

        val mimeType = MimeType.valueOf(requestBody.mimeType)

        val keyName: String = requestBody.key ?: String.format("%s.%s", UUID.randomUUID(), mimeType.subtype)

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .contentType(mimeType.toString())
            .key(keyName)
            .build()

        val putObjectPresignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofDays(1))
            .putObjectRequest(putObjectRequest)
            .build()

        val presignedUrl: URL = s3Presigner.presignPutObject(putObjectPresignRequest).url()

        val responseBody = ResponseBody(key = keyName, uploadUrl = presignedUrl.toString())

        return ServerResponse.ok()
            .body(BodyInserters.fromValue(responseBody))
            .awaitSingle()
    }
}

@JvmRecord
internal data class RequestBody(val key: String?, val mimeType: String)

internal data class ResponseBody(val key: String, val uploadUrl: String)