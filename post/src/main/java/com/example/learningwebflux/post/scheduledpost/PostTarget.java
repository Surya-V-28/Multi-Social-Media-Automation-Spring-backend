package com.example.learningwebflux.post.scheduledpost;

import com.example.learningwebflux.post.PostTargetType;
import com.example.learningwebflux.post.scheduledpost.posttargetdetails.PostTargetDetails;

public class PostTarget {
    public PostTarget(
        String id,
        String platformConnectionId,
        PostTargetType targetType,
        PostTargetDetails details,
        String createdPostId
    ) {
        this.id = id;
        this.platformConnectionId = platformConnectionId;
        this.targetType = targetType;
        this.details = details;
        this.createdPostId = createdPostId;
    }

    public void setPostId(String value) {
        this.createdPostId = value;
    }


    public final String id;
    public final String platformConnectionId;
    public final PostTargetType targetType;
    public final PostTargetDetails details;
    public String createdPostId;
}
