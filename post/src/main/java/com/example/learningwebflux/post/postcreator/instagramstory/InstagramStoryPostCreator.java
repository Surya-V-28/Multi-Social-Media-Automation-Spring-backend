package com.example.learningwebflux.post.postcreator.instagramstory;

import com.example.learningwebflux.post.postcreator.PostCreationParameters;
import com.example.learningwebflux.post.postcreator.PostCreator;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.InstagramStoryPostTargetDetails;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.PostTargetDetails;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class InstagramStoryPostCreator implements PostCreator {
    @Override
    public Mono<String> createPost(
        PostTargetDetails aPostTargetDetails,
        String accessToken,
        PostCreationParameters postCreationParameters
    ) {
        var postTargetDetails = (InstagramStoryPostTargetDetails) aPostTargetDetails;

        var postMedia = postCreationParameters.postMedias().getFirst();
        return createMediaContainer(postMedia, postTargetDetails.userId(), accessToken)
            .flatMap(mediaId -> publishMediaContainer(postTargetDetails.userId(), accessToken, mediaId));
    }

    private Mono<String> createMediaContainer(
        PostCreationParameters.PostMedia postMedia,
        String userId,
        String accessToken
    ) {
        var requestBody = new HashMap<>(Map.of(
            "media_type", "STORIES",
            "access_token", accessToken
        ));

        if (postMedia.mediaType().getType().equalsIgnoreCase("image")) {
            requestBody.put("image_url", postMedia.url().toString());
        }
        else if (postMedia.mediaType().getType().equalsIgnoreCase("video")) {
            requestBody.put("video_url", postMedia.url().toString());
        }


        return webClient.post()
            .uri( String.format("https://graph.facebook.com/v20.0/%s/media", userId) )
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestBody))
            .exchangeToMono(response -> response.bodyToMono(JsonNode.class))
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
            .map(responseBody -> responseBody.get("id").asText());
    }


    private final WebClient webClient;
}
