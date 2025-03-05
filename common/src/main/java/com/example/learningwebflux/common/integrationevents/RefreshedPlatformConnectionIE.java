package com.example.learningwebflux.common.integrationevents;

public record RefreshedPlatformConnectionIE(
    String id,
    String newAccessToken
) {}
