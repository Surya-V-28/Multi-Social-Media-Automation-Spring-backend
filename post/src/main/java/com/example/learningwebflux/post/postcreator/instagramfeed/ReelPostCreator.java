package com.example.learningwebflux.post.postcreator.instagramfeed;

import com.example.learningwebflux.post.postcreator.PostCreationParameters;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.InstagramFeedPostTargetDetails;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
@AllArgsConstructor
public class ReelPostCreator {
    public Mono<String> createPost(
        InstagramFeedPostTargetDetails postTargetDetails,
        String accessToken,
        PostCreationParameters postCreationParameters
    ) {
        var videoUrl = postCreationParameters.postMedias().getFirst().url();

        return createMediaContainer(videoUrl, postTargetDetails.userId(), accessToken)
            .flatMap(mediaId -> {
                return uploadWaiter.waitForUpload(mediaId, accessToken)
                    .then(publishMediaContainer(postTargetDetails.userId(), accessToken, mediaId));
            });
    }

    private Mono<String> createMediaContainer(URI videoUrl, String userId, String accessToken) {
        var requestBody = Map.of(
            "media_type", "REELS",
            "video_url", videoUrl.toString(),
            "access_token", accessToken
        );

        return webClient.post()
            .uri( String.format("https://graph.facebook.com/v20.0/%s/media", userId) )
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestBody))
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(responseBody -> responseBody.get("id").asText());
    }

    private Mono<String> publishMediaContainer(String userId, String accessToken, String mediaId) {
        return webClient.post()
            .uri( URI.create(String.format("https://graph.facebook.com/v20.0/%s/media_publish", userId)) )
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                BodyInserters.fromValue(
                    Map.of(
                        "creation_id", mediaId,
                        "access_token", accessToken
                    )
                )
            )
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .doOnError(WebClientResponseException.class, exception -> {
                System.out.printf("Failed to publish reel: %n%s%n", exception.getResponseBodyAsString());
            })
            .map(responseBody -> responseBody.get("id").asText());
    }


    private final WebClient webClient;
    private final MediaUploadWaiter uploadWaiter;
}
