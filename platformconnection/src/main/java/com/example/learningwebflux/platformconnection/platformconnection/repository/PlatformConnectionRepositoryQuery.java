package com.example.learningwebflux.platformconnection.platformconnection.repository;

import com.example.learningwebflux.platformconnection.Platform;
import lombok.Builder;

@Builder
public class PlatformConnectionRepositoryQuery {
    public final String id;
    public final String userId;
    public final Platform platform;
    public final String platformUserId;
}
