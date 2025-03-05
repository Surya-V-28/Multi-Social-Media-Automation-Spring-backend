package com.example.learningwebflux.post.postcreator;

import com.example.learningwebflux.post.PostTargetType;
import com.example.learningwebflux.post.postcreator.facebookpage.FacebookPagePostCreator;
import com.example.learningwebflux.post.postcreator.instagramfeed.InstagramFeedPostCreator;
import com.example.learningwebflux.post.postcreator.instagramstory.InstagramStoryPostCreator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PostCreatorRegistry {
    public PostCreatorRegistry(
        InstagramFeedPostCreator instagramFeedPostCreator,
        FacebookPagePostCreator facebookPagePostCreator,
        InstagramStoryPostCreator instagramStoryPostCreator
    ) {
        postCreators = Map.of(
            PostTargetType.facebookPage, facebookPagePostCreator,
            PostTargetType.instagramFeed, instagramFeedPostCreator,
            PostTargetType.instagramStory, instagramStoryPostCreator
        );
    }

    public PostCreator getPostCreator(PostTargetType postTargetType) {
        return postCreators.get(postTargetType);
    }


    private final Map<PostTargetType, PostCreator> postCreators;
}