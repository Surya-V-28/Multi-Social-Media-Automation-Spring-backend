package com.example.learningwebflux.post.commands.fulfillscheduledpost;

import com.example.learningwebflux.common.integrationeventbus.IntegrationEventBus;
import com.example.learningwebflux.common.integrationevents.PostedScheduledPostIE;
import com.example.learningwebflux.post.PostMedia;
import com.example.learningwebflux.post.platformconnection.repository.PlatformConnectionRepository;
import com.example.learningwebflux.post.postcreator.PostCreationParameters;
import com.example.learningwebflux.post.postcreator.PostCreatorRegistry;
import com.example.learningwebflux.post.scheduledpost.PostTarget;
import com.example.learningwebflux.post.scheduledpost.ScheduledPost;
import com.example.learningwebflux.post.scheduledpost.repository.ScheduledPostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URI;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
public class FulfillScheduledPostCommandHandler {
    public FulfillScheduledPostCommandHandler(
        ScheduledPostRepository scheduledPostRepository,
        PlatformConnectionRepository platformConnectionRepository,
        PostCreatorRegistry postCreatorRegistry,
        IntegrationEventBus integrationEventBus,

        S3AsyncClient s3Client,
        S3Presigner s3Presigner,
        @Value("${aws.s3.bucket}") String s3Bucket
    ) {
        this.scheduledPostRepository = scheduledPostRepository;
        this.platformConnectionRepository = platformConnectionRepository;
        this.postCreatorRegistry = postCreatorRegistry;
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.integrationEventBus = integrationEventBus;
        this.s3Bucket = s3Bucket;
    }

    public Mono<Void> perform(String id) {
        return scheduledPostRepository.getWithId(id)
            .flatMap(scheduledPost -> {
                return createPosts(scheduledPost)
                    .then(Mono.defer(() -> {
                        scheduledPost.markAsFulfilled();

                        return scheduledPostRepository.save(scheduledPost);
                    }))
                    .then(Mono.fromRunnable(() -> {
                        integrationEventBus.publish(new PostedScheduledPostIE(id));
                    }));
            });
    }

    private Mono<Void> createPosts(ScheduledPost scheduledPost) {
        return getAccessTokens(scheduledPost)
            .flatMap(accessTokens -> {
                return getMediaUrls(scheduledPost.getMedias())
                    .flatMap(postMedias -> {
                        return Flux.fromIterable(scheduledPost.getTargets())
                            .flatMap(target -> {
                                var postCreator = postCreatorRegistry.getPostCreator(target.targetType);

                                var postCreationParameters = new PostCreationParameters(
                                    scheduledPost.getTitle(),
                                    scheduledPost.getCaption(),
                                    postMedias
                                );
                                return postCreator.createPost(
                                    target.details,
                                    accessTokens.get(target.platformConnectionId),
                                    postCreationParameters
                                )
                                    .flatMap(createdPostId -> {
                                        target.setPostId(createdPostId);
                                        return Mono.empty();
                                    });
                            })
                            .then();
                    });
            });
    }

    private Mono<Map<String, String>> getAccessTokens(ScheduledPost scheduledPost) {
        return Flux.fromIterable(scheduledPost.getTargets())
            .reduce(
                new HashSet<String>(),
                (HashSet<String> platformConnectionIdsSet, PostTarget postTarget) -> {
                    platformConnectionIdsSet.add(postTarget.platformConnectionId);

                    return platformConnectionIdsSet;
                }
            )
            .flatMapIterable(platformConnectionIdsSet -> platformConnectionIdsSet)
            .flatMap(platformConnectionRepository::withId)
            .collectMap(
                platformConnection -> platformConnection.id,
                platformConnection -> platformConnection.accessToken
            );

    }

    private Mono<List<PostCreationParameters.PostMedia>> getMediaUrls(List<PostMedia> medias) {
        return Flux.fromIterable(medias)
            .flatMap(media -> {
                var getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofDays(1))
                    .getObjectRequest( GetObjectRequest.builder().bucket(s3Bucket).key(media.fileId()).build() )
                    .build();

                var url = URI.create(s3Presigner.presignGetObject(getObjectPresignRequest).url().toString());

                return Mono.fromFuture(
                    s3Client.headObject(
                        HeadObjectRequest.builder()
                            .bucket(s3Bucket)
                            .key(media.fileId())
                            .build()
                    )
                )
                    .map(headObjectResponse -> {
                        var mimeType = MimeType.valueOf(headObjectResponse.contentType());
                        return new PostCreationParameters.PostMedia(mimeType, url);
                    });
            })
            .collectList();
    }


    private final ScheduledPostRepository scheduledPostRepository;
    private final PlatformConnectionRepository platformConnectionRepository;
    private final PostCreatorRegistry postCreatorRegistry;
    private final IntegrationEventBus integrationEventBus;

    private final String s3Bucket;
    private final S3AsyncClient s3Client;
    private final S3Presigner s3Presigner;
}
