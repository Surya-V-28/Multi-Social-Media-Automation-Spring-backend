package com.example.learningwebflux.medialibrary.utils

import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.net.URL
import java.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

internal class S3Utils {
    companion object {
        public fun createPresignedUrl(s3Presigner: S3Presigner, bucketName: String, keyName: String): URL {
            val getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

            val presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(1.days.toJavaDuration())
                .build()

            val presignedRequest = s3Presigner.presignGetObject(presignRequest)

            return presignedRequest.url()
        }
    }
}