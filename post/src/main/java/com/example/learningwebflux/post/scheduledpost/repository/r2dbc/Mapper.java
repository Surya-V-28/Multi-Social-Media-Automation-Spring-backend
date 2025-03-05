package com.example.learningwebflux.post.scheduledpost.repository.r2dbc;

import com.example.learningwebflux.post.PostMedia;
import com.example.learningwebflux.post.scheduledpost.ScheduledPost;
import com.example.learningwebflux.post.r2dbc.models.ScheduledPostDataModel;
import com.example.learningwebflux.post.scheduledpost.repository.r2dbc.datamodel.PostMediaDataModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.util.List;

@Component()
@AllArgsConstructor
class Mapper {
    public ScheduledPostDataModel toDataModel(ScheduledPost domainModel) throws JsonProcessingException {
        return new ScheduledPostDataModel(
            domainModel.id,
            domainModel.userId,
            domainModel.getScheduledTime(),
            domainModel.getTitle(),
            domainModel.getCaption(),
            Json.of(
                objectMapper.writeValueAsString(
                    domainModel.getMedias()
                        .stream()
                        .map(this::postMediaToDataModel)
                        .toList()
                )
            ),
            Json.of( objectMapper.writeValueAsString(domainModel.getTargets()) ),
            domainModel.getIsFulfilled(),
            domainModel.getSchedulerId()
        );
    }

    public ScheduledPost toDomainModel(ScheduledPostDataModel dataModel) throws JsonProcessingException {
        return new ScheduledPost(
            dataModel.id,
            dataModel.userId,
            dataModel.scheduledTime,
            objectMapper.readValue(dataModel.targets.asString(), new TypeReference<>() {}),
            dataModel.title,
            dataModel.caption,
            objectMapper.readValue(dataModel.medias.asString(), new TypeReference<List<PostMediaDataModel>>() {})
                .stream()
                .map(this::postMediaToDomainModel)
                .toList(),
            dataModel.isFulfilled,
            dataModel.schedulerId
        );
    }

    public PostMediaDataModel postMediaToDataModel(PostMedia domainModel) {
        return new PostMediaDataModel(domainModel.id(), domainModel.fileId());
    }

    public PostMedia postMediaToDomainModel(PostMediaDataModel dataModel) {
        return new PostMedia(dataModel.id(), dataModel.fileId());
    }


    private final ObjectMapper objectMapper;
}
