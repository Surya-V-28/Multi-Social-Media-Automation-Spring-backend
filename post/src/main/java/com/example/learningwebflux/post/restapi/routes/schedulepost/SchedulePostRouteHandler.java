package com.example.learningwebflux.post.restapi.routes.schedulepost;

import com.example.learningwebflux.post.PostTargetType;
import com.example.learningwebflux.post.commands.schedulepost.SchedulePostCommand;
import com.example.learningwebflux.post.commands.schedulepost.SchedulePostCommandHandler;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.FacebookPagePostTargetDetails;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.InstagramFeedPostTargetDetails;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.InstagramStoryPostTargetDetails;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.PostTargetDetails;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Component
@AllArgsConstructor
public class SchedulePostRouteHandler {
    public Mono<ServerResponse> handle(ServerRequest request) {
        return ReactiveSecurityContextHolder.getContext()
            .flatMap(securityContext -> {
                return request.bodyToMono(JsonNode.class)
                    .flatMap(requestBody -> {
                        var command = new SchedulePostCommand(
                            request.pathVariable("userId"),
                            OffsetDateTime.parse(requestBody.get("scheduledTime").asText()),
                            StreamSupport.stream(requestBody.get("targets").spliterator(), false)
                                .map(this::mapPostTargetRequestBodyToCommand)
                                .toList(),
                            requestBody.get("title").asText(),
                            requestBody.get("caption").asText(),
                            StreamSupport.stream(requestBody.get("media").spliterator(), false)
                                .map(JsonNode::asText)
                                .toList()
                        );

                        return commandHandler.perform(command);
                    })
                    .flatMap(scheduledPostId -> {
                        var responseBody = new SchedulePostRouteResponseBody(scheduledPostId);

                        return ServerResponse.ok()
                            .body(BodyInserters.fromValue(responseBody));
                    });
            });
    }

    private SchedulePostCommand.PostTarget mapPostTargetRequestBodyToCommand(JsonNode requestBody) {
        var targetType = requestBody.get("targetType").asText();

        PostTargetDetails details;
        if (Objects.equals(targetType, PostTargetType.facebookPage.name())) {
            details = new FacebookPagePostTargetDetails(requestBody.get("details").get("pageId").asText());
        }
        else if (Objects.equals(targetType, PostTargetType.instagramFeed.name())) {
            details = new InstagramFeedPostTargetDetails(requestBody.get("details").get("userId").asText());
        }
        else {
            details = new InstagramStoryPostTargetDetails(requestBody.get("details").get("userId").asText());
        }

        return new SchedulePostCommand.PostTarget(
            requestBody.get("platformConnectionId").asText(),
            PostTargetType.valueOf(requestBody.get("targetType").asText()),
            details
        );
    }



    private final SchedulePostCommandHandler commandHandler;
}