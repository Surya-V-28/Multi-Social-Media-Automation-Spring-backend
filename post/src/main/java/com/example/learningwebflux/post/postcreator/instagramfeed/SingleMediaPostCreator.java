package com.example.learningwebflux.post.postcreator.instagramfeed;

import com.example.learningwebflux.post.postcreator.PostCreationParameters;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.InstagramFeedPostTargetDetails;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.PostTargetDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
@AllArgsConstructor
public class SingleMediaPostCreator {
    public Mono<String> createPost(
        InstagramFeedPostTargetDetails postTargetDetails,
        String accessToken,
        PostCreationParameters postCreationParameters
    ) {
        var imageUrl = postCreationParameters.postMedias().getFirst().url();

        return createImageMediaContainer(imageUrl, postTargetDetails.userId(), accessToken)
            .flatMap(mediaId -> publishMediaContainer(postTargetDetails.userId(), accessToken, mediaId));
    }

    private Mono<String> createImageMediaContainer(URI imageUrl, String userId, String accessToken) {
        var requestBody = Map.of(
            "image_url", imageUrl.toString(),
            "access_token", accessToken
        );

//        return webClient.post()
//            .uri( String.format("https://graph.facebook.com/v20.0/%s/media", userId) )
//            .contentType(MediaType.APPLICATION_JSON)
//            .body(BodyInserters.fromValue(requestBody))
//            .retrieve()
//            .bodyToMono(JsonNode.class)
//            .map(responseBody -> responseBody.get("id").asText());

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
            .map(responseBody -> responseBody.get("id").asText());
    }


    private final WebClient webClient;
    private final ObjectMapper objectMapper;
}
