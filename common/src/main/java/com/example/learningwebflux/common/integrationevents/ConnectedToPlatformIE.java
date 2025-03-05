package com.example.learningwebflux.common.integrationevents;

public record ConnectedToPlatformIE(
    String id,
    String userId,
    String platform,
    String platformUserId,
    String accessToken
) { }