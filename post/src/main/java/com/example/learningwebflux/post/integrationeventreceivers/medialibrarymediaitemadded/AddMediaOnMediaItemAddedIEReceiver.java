package com.example.learningwebflux.post.integrationeventreceivers.medialibrarymediaitemadded;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventReceiver;
import com.example.learningwebflux.common.integrationevents.medialibrarymediaadded.ImageMediaDetails;
import com.example.learningwebflux.common.integrationevents.medialibrarymediaadded.MediaDetails;
import com.example.learningwebflux.common.integrationevents.medialibrarymediaadded.MediaLibraryMediaAddedIE;
import com.example.learningwebflux.common.integrationevents.medialibrarymediaadded.VideoMediaDetails;
import com.example.learningwebflux.post.media.Media;
import com.example.learningwebflux.post.media.repository.MediaRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AddMediaOnMediaItemAddedIEReceiver implements IntegrationEventReceiver<MediaLibraryMediaAddedIE> {
    public AddMediaOnMediaItemAddedIEReceiver(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public Mono<Void> receive(MediaLibraryMediaAddedIE event) {
        var media = new Media(
            event.id(),
            event.userId(),
            event.name(),
            event.mimeType(),
            event.size(),
            mapMediaDetails(event.mediaDetails())
        );
        return mediaRepository.save(media);
    }

    public com.example.learningwebflux.post.media.MediaDetails mapMediaDetails(MediaDetails eventItem) {
        com.example.learningwebflux.post.media.MediaDetails mediaDetails;
        if (eventItem instanceof ImageMediaDetails imageEventItem) {
            mediaDetails = new com.example.learningwebflux.post.media.ImageMediaDetails(imageEventItem.width(), imageEventItem.height());
        }
        else if (eventItem instanceof VideoMediaDetails videoEventItem) {
            mediaDetails = new com.example.learningwebflux.post.media.VideoMediaDetails(videoEventItem.duration());
        }
        else {
            throw new RuntimeException("Exhausted MediaDetails type when matching");
        }

        return mediaDetails;
    }




    private final MediaRepository mediaRepository;
}
