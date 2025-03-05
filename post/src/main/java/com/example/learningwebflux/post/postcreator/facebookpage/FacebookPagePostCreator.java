package com.example.learningwebflux.post.postcreator.facebookpage;

import com.example.learningwebflux.post.postcreator.PostCreationParameters;
import com.example.learningwebflux.post.postcreator.PostCreator;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.FacebookPagePostTargetDetails;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.PostTargetDetails;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.stream.StreamSupport;

@Component
@AllArgsConstructor
public class FacebookPagePostCreator implements PostCreator {
    @Override
    public Mono<String> createPost(
        PostTargetDetails postTargetDetails,
        String accessToken,
        PostCreationParameters postCreationParameters
    ) {
        var facebookPagePostTargetDetails = (FacebookPagePostTargetDetails) postTargetDetails;

        return getPageAccessToken(facebookPagePostTargetDetails.pageId(), accessToken)
            .flatMap(pageAccessToken -> {
                var isImagePost = postCreationParameters.postMedias()
                    .stream()
                    .noneMatch(e -> e.mediaType().getType().equals("video"));

                if (isImagePost) {
                    return imagePostCreator.createPost(postTargetDetails, postCreationParameters, pageAccessToken);
                }
                else {
                    return videoPostCreator.createPost(postTargetDetails, accessToken, postCreationParameters, pageAccessToken);
                }
            });
    }

    private Mono<String> getPageAccessToken(String pageId, String accessToken) {
        return webClient.get()
            .uri(String.format("https://graph.facebook.com/me/accounts?access_token=%s", accessToken))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(responseBody -> {
                return StreamSupport.stream(responseBody.get("data").spliterator(), false)
                    .filter(node -> node.get("id").asText().equals(pageId))
                    .findFirst().orElseThrow()
                    .get("access_token")
                    .asText();
            });
    }


    private final WebClient webClient;
    private final ImagePostCreator imagePostCreator;
    private final VideoPostCreator videoPostCreator;
}
