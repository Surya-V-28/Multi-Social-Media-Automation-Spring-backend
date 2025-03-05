package com.example.learningwebflux.post.restapi.routes.gets3uploadurl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Component
public class GetS3UploadUrlRouteHandler {
    public GetS3UploadUrlRouteHandler(S3Presigner s3Presigner, @Value("${aws.s3.bucket}") String bucketName) {
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
    }

    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.bodyToMono(GetS3UploadUrlRouteRequestBody.class)
            .flatMap(requestBody -> {
                MimeType mimeType = MimeType.valueOf(requestBody.mimeType());

                String keyName;
                if (requestBody.key() != null) {
                    keyName = requestBody.key();
                }
                else {
                    keyName = String.format("%s.%s", UUID.randomUUID(), mimeType.getSubtype());
                }

                var putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .contentType( mimeType.toString() )
                    .key(keyName)
                    .build();

                var putObjectPresignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofDays(1))
                    .putObjectRequest(putObjectRequest)
                    .build();

                var presignedUrl = s3Presigner.presignPutObject(putObjectPresignRequest).url();

                var responseBody = Map.of(
                    "key", keyName,
                    "uploadUrl", presignedUrl
                );

                return ServerResponse.ok()
                    .body(BodyInserters.fromValue(responseBody));
            });
    }


    private final S3Presigner s3Presigner;
    private final String bucketName;
}

record GetS3UploadUrlRouteRequestBody(
    String key,
    String mimeType
) {}