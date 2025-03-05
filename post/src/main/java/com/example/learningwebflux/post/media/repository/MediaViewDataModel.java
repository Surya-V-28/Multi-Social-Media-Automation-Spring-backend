package com.example.learningwebflux.post.media.repository;

import com.example.learningwebflux.post.media.ImageMediaDetails;
import com.example.learningwebflux.post.media.Media;
import com.example.learningwebflux.post.media.VideoMediaDetails;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.MimeType;

import java.time.Duration;

@Table("media_view")
public record MediaViewDataModel(
    String id,
    String userId,
    String name,
    String mimeType,
    long size,

    Integer width,
    Integer height,

    Long duration
) {
    Media toDomainModel() {
        return new Media(
            id,
            userId,
            name,
            MimeType.valueOf(mimeType),
            size,
            MimeType.valueOf(mimeType).getType().equals("image")
                ? new ImageMediaDetails(width, height)
                : new VideoMediaDetails(Duration.ofSeconds(duration))
        );
    }
}