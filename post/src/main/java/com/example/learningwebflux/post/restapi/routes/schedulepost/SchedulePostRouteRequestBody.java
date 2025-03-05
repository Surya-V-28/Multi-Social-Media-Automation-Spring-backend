package com.example.learningwebflux.post.restapi.routes.schedulepost;

import com.example.learningwebflux.post.PostTargetType;
import com.example.learningwebflux.post.commands.schedulepost.SchedulePostCommand;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public record SchedulePostRouteRequestBody(
    OffsetDateTime scheduledTime,
    List<PostTarget> targets,
    String title,
    String caption,
    List<String> media
) {
    public record PostTarget(
        PostTargetType targetType,
        String platformConnectionId,
        JsonNode details
    ) { }
}
