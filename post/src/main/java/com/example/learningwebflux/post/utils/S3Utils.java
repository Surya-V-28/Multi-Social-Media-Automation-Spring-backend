package com.example.learningwebflux.post.utils;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

public class S3Utils {
    public static URL createPresignedUrl(S3Presigner s3Presigner, String bucketName, String keyName) {
        var getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(keyName)
            .build();

        var presignRequest = GetObjectPresignRequest.builder()
            .getObjectRequest(getObjectRequest)
            .signatureDuration(Duration.ofDays(1))
            .build();

        var presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url();
    }
}