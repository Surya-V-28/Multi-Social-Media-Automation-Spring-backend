package com.example.learningwebflux.platformconnection.r2dbc.datamodels;

import com.example.learningwebflux.platformconnection.Platform;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Table("platform_connection")
@AllArgsConstructor
public class PlatformConnectionDataModel {
    @Id
    public String id;
    public String userId;
    public String platform;
    public String platformUserId;
    public String refreshToken;
    public String accessToken;
    public OffsetDateTime expiresAt;
}