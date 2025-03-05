package com.example.learningwebflux.post.scheduledpostvalidator;

import org.springframework.util.MimeType;

import java.time.Duration;
import java.util.List;

public record ValidatingPost(
    String title,
    String caption,
    List<Media> media
) {
    public record Media(int size, MimeType mimeType, MediaDetails details) { }

    public interface MediaDetails {}

    public record ImageMediaDetails(int width, int height) implements MediaDetails { }

    public record VideoMediaDetails(Duration length) implements MediaDetails { }
}
