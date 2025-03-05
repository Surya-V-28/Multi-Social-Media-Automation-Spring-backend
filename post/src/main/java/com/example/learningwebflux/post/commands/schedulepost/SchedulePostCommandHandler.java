package com.example.learningwebflux.post.commands.schedulepost;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventBus;
import com.example.learningwebflux.common.integrationevents.PostScheduledIE;
import com.example.learningwebflux.post.PostMedia;
import com.example.learningwebflux.post.scheduledpost.PostTarget;
import com.example.learningwebflux.post.scheduledpost.ScheduledPost;
import com.example.learningwebflux.post.scheduledpost.repository.ScheduledPostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.util.UUID;

@Component
public class SchedulePostCommandHandler {
    public SchedulePostCommandHandler(
        ScheduledPostRepository scheduledPostRepository,
        S3AsyncClient s3Client,
        @Value("${aws.s3.bucket}") String s3Bucket,
        IntegrationEventBus integrationEventBus
    ) {
        this.scheduledPostRepository = scheduledPostRepository;
        this.s3Client = s3Client;
        this.s3Bucket = s3Bucket;
        this.integrationEventBus = integrationEventBus;
    }

    public Mono<String> perform(SchedulePostCommand command) {
        return Flux.fromIterable(command.postMedias())
            .flatMap(this::getS3QueryData)
            .flatMap(s3QueryData -> {
                if (!s3QueryData.headObjectResponse().sdkHttpResponse().isSuccessful()) {
                    return Mono.error(new Exception("Media is missing"));
                }

                return Mono.just(s3QueryData);
            })
            .collectList()
            .flatMap(s3QueryData -> {
                var postMedias = s3QueryData
                    .stream()
                    .map( element -> new PostMedia(UUID.randomUUID().toString(), element.key()) )
                    .toArray(PostMedia[]::new);

                var postTargets = command.postTargets()
                    .stream()
                    .map(e -> new PostTarget(UUID.randomUUID().toString(), e.platformConnectionId(), e.targetType(), e.details(), null))
                    .toArray(PostTarget[]::new);

                ScheduledPost scheduledPost;
                try {
                    scheduledPost = ScheduledPost.builder(
                        UUID.randomUUID().toString(),
                        command.userId(),
                        command.scheduledTime()
                    )
                        .postTargets(postTargets)
                        .title(command.title())
                        .caption(command.caption())
                        .postMedias(postMedias)
                        .build();
                }
                catch (Exception exception) { return Mono.error(exception); }

                return scheduledPostRepository.save(scheduledPost)
                    .then(Mono.fromRunnable(() -> {
                        var event = mapScheduledPostToIntegrationEvent(scheduledPost);
                        integrationEventBus.publish(event);
                    }))
                    .then(Mono.just(scheduledPost.id));
            });
    }

    private Mono<S3QueryData> getS3QueryData(String key) {
        return Mono.fromFuture(
            s3Client.headObject(
                HeadObjectRequest.builder()
                    .bucket(s3Bucket)
                    .key(key)
                    .build()
            )
        )
            .map(headObjectResponse -> new S3QueryData(key, headObjectResponse));
    }

    private PostScheduledIE mapScheduledPostToIntegrationEvent(ScheduledPost scheduledPost) {
        return new PostScheduledIE(
            scheduledPost.id,
            scheduledPost.userId,
            scheduledPost.getScheduledTime(),
            scheduledPost.getTargets()
                .stream()
                .map((target) -> {
                    return new PostScheduledIE.PostTarget(
                        target.id,
                        target.platformConnectionId,
                        target.targetType.toString(),
                        target.createdPostId
                    );
                })
                .toList(),
            scheduledPost.getTitle(),
            scheduledPost.getCaption(),
            scheduledPost.getMedias()
                .stream()
                .map((media) -> {
                    return new PostScheduledIE.PostMedia(
                        media.id(),
                        media.fileId()
                    );
                })
                .toList()
        );
    }



    private final ScheduledPostRepository scheduledPostRepository;
    private final S3AsyncClient s3Client;
    private final String s3Bucket;
    private final IntegrationEventBus integrationEventBus;
}

record S3QueryData(String key, HeadObjectResponse headObjectResponse) {}
