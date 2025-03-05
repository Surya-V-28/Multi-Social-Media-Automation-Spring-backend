package com.example.learningwebflux.post.restapi.routes.validatepost;

import com.example.learningwebflux.post.PostTargetType;
import com.example.learningwebflux.post.commands.ValidatePostCommandHandler;
import com.example.learningwebflux.post.scheduledpostvalidator.ValidatingPost;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValidatePostRouteHandler {
    public ValidatePostRouteHandler(ValidatePostCommandHandler commandHandler, ObjectMapper objectMapper) {
        this.commandHandler = commandHandler;
        this.objectMapper = objectMapper;
    }

    public Mono<ServerResponse> handle(ServerRequest request) {
        return request
            .bodyToMono(RequestBody.class)
            .flatMap(requestBody -> {
                var validatingPost = mapValidatingPostToCommandArgs(requestBody.validatingPost());
                return commandHandler.validate(requestBody.targets(), validatingPost);
            })
            .flatMap(errors -> {
                return ServerResponse.ok()
                    .body(BodyInserters.fromValue(errors));
            });
    }

    ValidatingPost mapValidatingPostToCommandArgs(RequestBody.ValidatingPost networkModel) {
        return new ValidatingPost(
            networkModel.title(),
            networkModel.caption(),
            (networkModel.media() != null) ?
                networkModel.media()
                    .stream()
                    .map(mediaNetworkModel -> {
                        MimeType mimeType = MimeType.valueOf(mediaNetworkModel.mimeType());

                        return new ValidatingPost.Media(
                            mediaNetworkModel.size(),
                            mimeType,
                            mapMediaDetailsToCommandArgs(mediaNetworkModel.details(), mimeType)
                        );
                    })
                    .collect(Collectors.toList())
                : null
        );
    }

    ValidatingPost.MediaDetails mapMediaDetailsToCommandArgs(JsonNode aNetworkModel, MimeType mimeType) {
        if (mimeType.getType().equalsIgnoreCase("image")) {
            RequestBody.ImageMediaDetails networkModel;
            try { networkModel = objectMapper.readValue(aNetworkModel.toPrettyString(), RequestBody.ImageMediaDetails.class); }
            catch (JsonProcessingException e) { throw new RuntimeException(e); }

            return new ValidatingPost.ImageMediaDetails(networkModel.width(), networkModel.height());
        }
        else {
            RequestBody.VideoMediaDetails networkModel;
            try { networkModel = objectMapper.readValue(aNetworkModel.toPrettyString(), RequestBody.VideoMediaDetails.class); }
            catch (JsonProcessingException e) { throw new RuntimeException(e); }

            return new ValidatingPost.VideoMediaDetails(Duration.ofMillis(networkModel.length()));
        }
    }

    private final ValidatePostCommandHandler commandHandler;
    private final ObjectMapper objectMapper;
}

record RequestBody(
    List<PostTargetType> targets,
    ValidatingPost validatingPost
) {
    record ValidatingPost(
        String title,
        String caption,
        List<Media> media
    ) { }

    record Media(
        int size,
        String mimeType,
        JsonNode details
    ) {}

    record ImageMediaDetails(
        int width,
        int height
    ) { }

    record VideoMediaDetails(
        int length
    ) {}
}