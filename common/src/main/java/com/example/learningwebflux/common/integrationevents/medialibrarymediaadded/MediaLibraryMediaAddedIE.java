package com.example.learningwebflux.common.integrationevents.medialibrarymediaadded;

import org.springframework.util.MimeType;

public record MediaLibraryMediaAddedIE(
    String id,
    String userId,
    String name,
    MimeType mimeType,
    long size,
    MediaDetails mediaDetails
) {}