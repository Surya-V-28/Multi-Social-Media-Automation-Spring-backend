package com.example.learningwebflux.post.media.repository;

import com.example.learningwebflux.post.media.ImageMediaDetails;
import org.springframework.data.relational.core.mapping.Table;

@Table("image_media_details")
public record ImageMediaDetailsDataModel(
    String mediaId,
    int width,
    int height
) {
    public static ImageMediaDetailsDataModel fromDataModel(String mediaId, ImageMediaDetails domainModel) {
        return new ImageMediaDetailsDataModel(
            mediaId,
            domainModel.width(),
            domainModel.height()
        );
    }
}