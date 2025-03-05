package com.example.learningwebflux.post.commands.schedulepost;

import com.example.learningwebflux.post.PostTargetType;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.PostTargetDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public record SchedulePostCommand(
    String userId,
    OffsetDateTime scheduledTime,
    List<PostTarget> postTargets,
    String title,
    String caption,
    List<String> postMedias
) {
    public record PostTarget(
        String platformConnectionId,
        PostTargetType targetType,
        PostTargetDetails details
    ) {}
}