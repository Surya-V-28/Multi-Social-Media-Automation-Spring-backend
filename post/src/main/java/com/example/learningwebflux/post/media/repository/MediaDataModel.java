package com.example.learningwebflux.post.media.repository;


import com.example.learningwebflux.post.media.Media;
import org.springframework.data.relational.core.mapping.Table;

@Table("media")
public record MediaDataModel(
    String id,
    String userId,
    String name,
    String mimeType,
    long size
) {
    static MediaDataModel fromDomainModel(Media domainModel) {
        return new MediaDataModel(
            domainModel.id(),
            domainModel.userId(),
            domainModel.name(),
            domainModel.mimeType().toString(),
            domainModel.size()
        );
    }
}