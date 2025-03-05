package com.example.learningwebflux.post.postcreator.facebookpage;

import com.example.learningwebflux.post.postcreator.PostCreationParameters;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.FacebookPagePostTargetDetails;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.PostTargetDetails;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class ImagePostCreator {
    public Mono<String> createPost(
        PostTargetDetails postTargetDetails,
        PostCreationParameters postCreationParameters,
        String pageAccessToken
    ) {
        var facebookPagePostTargetDetails = (FacebookPagePostTargetDetails) postTargetDetails;

        return uploadImages(postCreationParameters.postMedias(), facebookPagePostTargetDetails.pageId(), pageAccessToken)
            .flatMap(mediaIds -> {
                return publishPost(
                    facebookPagePostTargetDetails.pageId(),
                    pageAccessToken,
                    mediaIds,
                    postCreationParameters.caption()
                );
            });
    }

    private Mono<List<String>> uploadImages(
        List<PostCreationParameters.PostMedia> postMedias,
        String pageId,
        String pageAccessToken
    ) {
        return Flux.fromIterable(postMedias)
            .flatMap(postMedia -> {
                return webClient.post()
                    .uri(String.format("https://graph.facebook.com/%s/photos?access_token=%s", pageId, pageAccessToken))
                    .accept(MediaType.APPLICATION_JSON)
                    .body(
                        BodyInserters.fromValue(
                            Map.of(
                                "url", postMedia.url().toString(),
                                "published", false
                            )
                        )
                    )
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(responseBody -> responseBody.get("id").asText());
            })
            .collectList();
    }

    private Mono<String> publishPost(String pageId, String pageAccessToken, List<String> mediaIds, String caption) {
        return webClient.post()
            .uri(String.format("https://graph.facebook.com/%s/feed?access_token=%s", pageId, pageAccessToken))
            .accept(MediaType.APPLICATION_JSON)
            .body(
                BodyInserters.fromValue(
                    Map.of(
                        "attached_media", mediaIds.stream()
                            .map(mediaId -> Map.of("media_fbid", mediaId) )
                            .toList(),
                        "message", caption
                    )
                )
            )
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(responseBody -> responseBody.get("id").asText());
    }


    private final WebClient webClient;
}