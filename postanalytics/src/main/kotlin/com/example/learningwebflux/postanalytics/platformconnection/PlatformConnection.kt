package com.example.learningwebflux.postanalytics.platformconnection

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
internal class PlatformConnection(
    @Id
    public val id: String,
    public val userId: String,
    public val platformUserId: String,
    public var accessToken: String?,
)