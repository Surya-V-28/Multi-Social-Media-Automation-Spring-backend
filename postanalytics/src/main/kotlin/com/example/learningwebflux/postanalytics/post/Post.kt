package com.example.learningwebflux.postanalytics.post

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
internal class Post(
    @Id
    public val id: String,
    public val userId: String,
    public val scheduledPostId: String,
    public val type: PostTargetType,
    public val platformConnectionId: String,
    public val createdPostId: String,
    public val createdAt: String,
)