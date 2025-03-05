package com.example.learningwebflux.platformconnection.platformconnection;

import com.example.learningwebflux.platformconnection.Platform;

import java.time.OffsetDateTime;

public class PlatformConnection {
    public PlatformConnection(
        String id,
        String userId,
        Platform platform,
        String platformUserId,
        String refreshToken,
        String accessToken,
        OffsetDateTime expiresAt
    ) {
        this.userId = userId;
        this.id = id;
        this.platform = platform;
        this.platformUserId = platformUserId;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }

    public void updateOnAuthorization(String newAccessToken, String newRefreshToken) {
        accessToken = newAccessToken;
        refreshToken = newRefreshToken;
    }

    public void invalidate() {
        accessToken = null;
        refreshToken = null;
        expiresAt = null;
    }

    public void setNewRefreshToken(String value) { refreshToken = value; }

    public String getRefreshToken() { return refreshToken; }

    public void setNewAccessToken(String accessToken, OffsetDateTime expiresAt) {
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }

    public String getAccessToken() { return accessToken; }

    public OffsetDateTime getExpiresAt() { return expiresAt; }


    public final String id;
    public final String userId;
    public final Platform platform;
    public final String platformUserId;
    private String refreshToken;
    private String accessToken;
    private OffsetDateTime expiresAt;
}