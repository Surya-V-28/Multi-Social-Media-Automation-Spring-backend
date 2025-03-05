package com.example.learningwebflux.post.r2dbc.models;

import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Table("scheduled_post")
@AllArgsConstructor
public class ScheduledPostDataModel {
    @Id
    public final String id;
    public final String userId;
    public final OffsetDateTime scheduledTime;
    public final String title;
    public final String caption;
    public final Json medias;
    public final Json targets;
    public final boolean isFulfilled;
    public final String schedulerId;
}