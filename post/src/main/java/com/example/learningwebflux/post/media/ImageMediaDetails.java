package com.example.learningwebflux.post.media;

public record ImageMediaDetails(
    int width,
    int height
) implements MediaDetails { }
