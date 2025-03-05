package com.example.learningwebflux.post.postcreator.instagramfeed;

import com.example.learningwebflux.post.postcreator.PostCreationParameters;
import com.example.learningwebflux.post.postcreator.PostCreator;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.InstagramFeedPostTargetDetails;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.PostTargetDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class InstagramFeedPostCreator implements PostCreator {
    @Override
    public Mono<String> createPost(
        PostTargetDetails postTargetDetails,
        String accessToken,
        PostCreationParameters postCreationParameters
    ) {
        var instagramFeedPostTargetDetails = (InstagramFeedPostTargetDetails) postTargetDetails;

        if (postCreationParameters.postMedias().size() == 1) {
            if (isReelPost(postCreationParameters.postMedias().getFirst())) {
                return reelPostCreator.createPost(instagramFeedPostTargetDetails, accessToken, postCreationParameters);
            }
            else {
                return singleMediaPostCreator.createPost(instagramFeedPostTargetDetails, accessToken, postCreationParameters);
            }
        }
        else {
            return carouselPostCreator.createPost(instagramFeedPostTargetDetails, accessToken, postCreationParameters);
        }
    }

    private boolean isReelPost(PostCreationParameters.PostMedia postMedia) {
        return postMedia.mediaType().getType().equals("video");
    }



    private final CarouselPostCreator carouselPostCreator;
    private final SingleMediaPostCreator singleMediaPostCreator;
    private final ReelPostCreator reelPostCreator;
}
