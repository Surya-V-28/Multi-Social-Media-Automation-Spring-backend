package com.example.learningwebflux.post.media.repository;

import com.example.learningwebflux.post.media.VideoMediaDetails;
import org.springframework.data.relational.core.mapping.Table;

@Table("video_media_details")
public record VideoMediaDetailsDataModel(
    String mediaId,
    long duration
) {
    public static VideoMediaDetailsDataModel toDataModel(String mediaId, VideoMediaDetails domainModel) {
        return new VideoMediaDetailsDataModel(
            mediaId,
            domainModel.duration().getSeconds()
        );
    }
}