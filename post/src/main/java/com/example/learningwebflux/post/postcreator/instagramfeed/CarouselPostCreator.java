package com.example.learningwebflux.post.postcreator.instagramfeed;

import com.example.learningwebflux.post.postcreator.PostCreationParameters;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.InstagramFeedPostTargetDetails;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class CarouselPostCreator {
    public Mono<String> createPost(
        InstagramFeedPostTargetDetails postTargetDetails,
        String accessToken,
        PostCreationParameters postCreationParameters
    ) {
        return createMedia(postCreationParameters.postMedias(), postTargetDetails.userId(), accessToken)
            .delayElement(Duration.ofSeconds(30))
            .flatMap(mediaIds -> createCarousel(postTargetDetails.userId(), accessToken, mediaIds, postCreationParameters.caption()))
            .flatMap(carouselId -> publishCarousel(postTargetDetails.userId(), accessToken, carouselId));
    }

    private Mono<List<String>> createMedia(List<PostCreationParameters.PostMedia> postMedias, String userId, String accessToken) {
        return Flux.fromIterable(postMedias)
            .flatMap(postMedia -> {
                Map<String, Object> requestBody;
                if (postMedia.mediaType().getType().equals("video")) {
                    requestBody = Map.of(
                        "media_type", "VIDEO",
                        "video_url", postMedia.url(),
                        "is_carousel_item", true,
                        "access_token", accessToken
                    );
                }
                else {
                    requestBody = Map.of(
                        "image_url", postMedia.url(),
                        "is_carousel_item", true,
                        "access_token", accessToken
                    );
                }

                return webClient.post()
                    .uri(String.format("https://graph.facebook.com/v20.0/%s/media", userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .flatMap(responseBody -> {
                        var mediaId = responseBody.get("id").asText();

                        return mediaUploadWaiter.waitForUpload(mediaId, accessToken)
                            .then(Mono.just(mediaId));
                    });
            })
            .collectList();
    }

    private Mono<String> createCarousel(String userId, String accessToken, List<String> mediaIds, String caption) {
        var requestBody = Map.of(
            "media_type", "CAROUSEL",
            "caption", caption,
            "children", mediaIds,
            "access_token", accessToken
        );

        return webClient.post()
            .uri( URI.create(String.format("https://graph.facebook.com/v20.0/%s/media", userId)) )
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestBody))
            .retrieve()
            .bodyToMono(JsonNode.class)
            .flatMap(responseBody -> {
                var mediaId = responseBody.get("id").asText();

                return mediaUploadWaiter.waitForUpload(mediaId, accessToken)
                    .then(Mono.just(mediaId));
            });
    }

    private Mono<String> publishCarousel(String userId, String accessToken, String carouselId) {
        return webClient.post()
            .uri( URI.create(String.format("https://graph.facebook.com/v20.0/%s/media_publish", userId)) )
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                BodyInserters.fromValue(
                    Map.of(
                        "creation_id", carouselId,
                        "access_token", accessToken
                    )
                )
            )
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(responseBody -> responseBody.get("id").asText());
    }


    private final WebClient webClient;
    private final MediaUploadWaiter mediaUploadWaiter;
}
