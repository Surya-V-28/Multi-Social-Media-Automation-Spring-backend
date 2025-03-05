package com.example.learningwebflux.post.postcreator;

import com.example.learningwebflux.post.scheduledpost.posttargetdetails.PostTargetDetails;
import reactor.core.publisher.Mono;

public interface PostCreator {
    public Mono<String> createPost(
        PostTargetDetails postTargetDetails,
        String accessToken,
        PostCreationParameters postCreationParameters
    );
}