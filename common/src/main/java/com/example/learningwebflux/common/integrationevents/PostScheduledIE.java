package com.example.learningwebflux.common.integrationevents;

import java.time.OffsetDateTime;
import java.util.List;

public record PostScheduledIE(
    String id,
    String userId,
    OffsetDateTime scheduledTime,
    List<PostTarget> targets,
    String title,
    String caption,
    List<PostMedia> postMedia
) {
    public record PostTarget(
        String id,
        String platformConnectionId,
        String postTargetType,
        String createdPostId
    ) {}

    public record PostMedia(
        String id,
        String fileId
    ) {}
}
