package com.example.learningwebflux.post;

public enum PostTargetType {
    facebookPage(Platform.facebook),
    instagramFeed(Platform.instagram),
    instagramStory(Platform.instagram);


    private final Platform platform;

    PostTargetType(Platform platform) {
        this.platform = platform;
    }
}
