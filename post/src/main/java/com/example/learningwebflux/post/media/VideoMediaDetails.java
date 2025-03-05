package com.example.learningwebflux.post.media;

import java.time.Duration;

public record VideoMediaDetails(
    Duration duration
) implements MediaDetails {}
