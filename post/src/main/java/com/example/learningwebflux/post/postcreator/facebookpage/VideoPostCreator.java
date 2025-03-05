package com.example.learningwebflux.post.postcreator.facebookpage;

import com.example.learningwebflux.post.postcreator.PostCreationParameters;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.FacebookPagePostTargetDetails;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.PostTargetDetails;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.UUID;


@Component
public class VideoPostCreator {
    public VideoPostCreator(
        @Value("${facebook.appId}") String appId,
        WebClient webClient
    ) {
        this.appId = appId;
        this.webClient = webClient;
    }

    public Mono<String> createPost(
        PostTargetDetails postTargetDetails,
        String accessToken,
        PostCreationParameters postCreationParameters,
        String pageAccessToken
    ) {
        var facebookPagePostTargetDetails = (FacebookPagePostTargetDetails) postTargetDetails;

        return downloadVideoFromUrl(postCreationParameters.postMedias().getFirst().url(), webClient)
            .flatMap(bytes -> {
                return createUploadSession(appId, bytes.length, accessToken)
                    .flatMap(uploadSessionId -> {
                        return uploadDataToUploadSession(uploadSessionId, bytes, accessToken)
                            .flatMap(fileHandle -> {
                                return postVideoToPage(fileHandle, facebookPagePostTargetDetails.pageId(), pageAccessToken);
                            });
                    });
            });
    }


    private Mono<byte[]> downloadVideoFromUrl(URI url, WebClient webClient) {
        return webClient.get()
            .uri(url)
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .retrieve()
            .bodyToMono(byte[].class);
    }

    private Mono<String> createUploadSession(String appId, int fileByteSize, String accessToken) {
        return webClient.post()
            .uri(URI.create(String.format("https://graph.facebook.com/v21.0/%s/uploads", appId)))
            .body(BodyInserters.fromValue(
                Map.of(
                    "file_name", String.format("%s.mp4", UUID.randomUUID().toString()),
                    "file_length", fileByteSize,
                    "file_type", "video/mp4",
                    "access_token", accessToken
                )
            ))
            .retrieve()
            .bodyToMono(JsonNode.class)
            .flatMap(responseBody -> {
                var responseBodyId = responseBody.get("id").asText();
                return Mono.just(responseBodyId);
            });
    }

    private Mono<String> uploadDataToUploadSession(String uploadSessionId, byte[] fileBytes, String accessToken) {
        return webClient.post()
            .uri( URI.create(String.format("https://graph.facebook.com/v21.0/%s", uploadSessionId)) )
            .header("Authorization", "OAuth " + accessToken)
            .header("file_offset", "0")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(BodyInserters.fromValue(fileBytes))
            .retrieve()
            .bodyToMono(JsonNode.class)
            .doOnError(WebClientResponseException.class, (exception) -> {
                System.out.printf("CustomLog: Failed to upload data to Upload Sesssion: %s%n", exception.getResponseBodyAsString());
            })
            .map(responseBody -> responseBody.get("h").asText());
    }

    private Mono<String> postVideoToPage(String fileHandle, String pageId, String pageAccessToken) {
        return webClient.post()
            .uri( URI.create(String.format("https://graph-video.facebook.com/v21.0/%s/videos", pageId)) )
            .body(BodyInserters.fromValue(
                Map.of(
                    "title", "Test Video Title",
                    "description", "Test Video Description",
                    "fbuploader_video_file_chunk", fileHandle,
                    "access_token", pageAccessToken
                )
            ))
            .retrieve()
            .bodyToMono(JsonNode.class)
            .doOnError(WebClientResponseException.class, exception -> {
                System.out.printf("CustomLog: Failed to publish video:%n%s%n", exception.getResponseBodyAsString());
            })
            .map(responseBody -> responseBody.get("id").asText());
    }


    private final WebClient webClient;
    private final String appId;
}