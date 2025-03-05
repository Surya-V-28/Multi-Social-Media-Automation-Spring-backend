package com.example.learningwebflux.post.media;

import org.springframework.util.MimeType;

public record Media(
    String id,
    String userId,
    String name,
    MimeType mimeType,
    long size,

    MediaDetails mediaDetails
) {}