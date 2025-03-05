package com.example.learningwebflux.post.scheduledpost.posttargetdetails;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "jackson_deserialize_type"
)
public interface PostTargetDetails { }

