package com.example.learningwebflux.platformconnection.restapi.routes.connecttoplatform;

import com.example.learningwebflux.platformconnection.Platform;

import java.time.OffsetDateTime;

public record ConnectToPlatformRouteRequestBody(
    Platform platform,
    String accessToken,
    String refreshToken,
    OffsetDateTime expiresAt
) { }
