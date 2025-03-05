package com.example.learningwebflux.post.media.repository;

import com.example.learningwebflux.post.media.ImageMediaDetails;
import com.example.learningwebflux.post.media.Media;
import com.example.learningwebflux.post.media.VideoMediaDetails;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Component
public class MediaRepository {
    public MediaRepository(@Qualifier("postEntityTemplate") R2dbcEntityTemplate entityTemplate) {
        this.entityTemplate = entityTemplate;
    }

    public Mono<Void> save(Media media) {
        return entityTemplate.select(MediaDataModel.class)
            .matching(query( where("id").is(media.id()) ))
            .exists()
            .flatMap(exists -> {
                var mediaDataModel = MediaDataModel.fromDomainModel(media);
                if (!exists) {
                    return entityTemplate.insert(mediaDataModel)
                        .flatMap(dataModel -> {
                            if (media.mimeType().getType().equals("image")) {
                                var mediaDetailsDataModel = ImageMediaDetailsDataModel.fromDataModel(
                                    media.id(),
                                    (ImageMediaDetails) media.mediaDetails()
                                );
                                return entityTemplate.insert(mediaDetailsDataModel).then();
                            }
                            else if (media.mimeType().getType().equals("video")) {
                                var mediaDetailsDataModel = VideoMediaDetailsDataModel.toDataModel(
                                    media.id(),
                                    (VideoMediaDetails) media.mediaDetails()
                                );
                                return entityTemplate.insert(mediaDetailsDataModel).then();
                            }
                            else {
                                return Mono.error(new Exception(String.format("Unknown Media type %s", mediaDataModel.mimeType()))).then();
                            }
                        });
                }
                else {
                    return entityTemplate.update(mediaDataModel).then()
                        .flatMap(dataModel -> {
                            if (media.mimeType().getType().equals("image")) {
                                var mediaDetailsDataModel = ImageMediaDetailsDataModel.fromDataModel(
                                    media.id(),
                                    (ImageMediaDetails) media.mediaDetails()
                                );
                                return entityTemplate.update(mediaDetailsDataModel).then();
                            }
                            else if (media.mimeType().getType().equals("video")) {
                                var mediaDetailsDataModel = VideoMediaDetailsDataModel.toDataModel(
                                    media.id(),
                                    (VideoMediaDetails) media.mediaDetails()
                                );
                                return entityTemplate.update(mediaDetailsDataModel).then();
                            }
                            else {
                                return Mono.error(new Exception(String.format("Unknown Media type %s", mediaDataModel.mimeType()))).then();
                            }
                        });
                }
            });
    }

    public Mono<Media> getWithId(String id) {
        return entityTemplate.select(MediaViewDataModel.class)
            .matching(query( where("id").is(id) ))
            .one()
            .map(MediaViewDataModel::toDomainModel);
    }


    private final R2dbcEntityTemplate entityTemplate;
}
