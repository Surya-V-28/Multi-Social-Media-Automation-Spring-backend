package com.example.learningwebflux.post.postcreator.instagramfeed;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@AllArgsConstructor
public class MediaUploadWaiter {
    public Mono<Void> waitForUpload(String mediaId, String accessToken) {
        return webClient.get()
            .uri( String.format("https://graph.facebook.com/v20.0/%s?access_token=%s", mediaId, accessToken) )
            .retrieve()
            .bodyToMono(JsonNode.class)
            .flatMap(responseBody -> {
                var status = responseBody.get("status").asText();
                if (status.equals("IN_PROGRESS")) {
                    return Mono.delay(Duration.ofSeconds(10))
                        .then(waitForUpload(mediaId, accessToken));
                }
                else if (status.equals("FINISHED")) {
                    return Mono.empty();
                }
                else {
                    return Mono.error(new Exception(String.format("Failed to upload Instagram media with id: %n%s%n", responseBody.toPrettyString())));
                }
            });
    }

    private final WebClient webClient;
}
