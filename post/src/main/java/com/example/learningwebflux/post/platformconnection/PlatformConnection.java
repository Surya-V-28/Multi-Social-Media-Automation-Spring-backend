package com.example.learningwebflux.post.platformconnection;

import com.example.learningwebflux.post.Platform;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("platform_connection")
public class PlatformConnection {
    public PlatformConnection(String id, String userId, Platform platform, String platformUserId, String accessToken) {
        this.id = id;
        this.userId = userId;
        this.platform = platform;
        this.platformUserId = platformUserId;
        this.accessToken = accessToken;
    }

    @Id
    public final String id;
    public final String userId;
    public final Platform platform;
    public final String platformUserId;
    public String accessToken;
}