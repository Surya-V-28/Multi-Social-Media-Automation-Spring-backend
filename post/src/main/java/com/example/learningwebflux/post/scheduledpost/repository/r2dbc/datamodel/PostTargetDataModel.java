package com.example.learningwebflux.post.scheduledpost.repository.r2dbc.datamodel;

import com.example.learningwebflux.post.PostTargetType;
import lombok.AllArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("post_target")
@AllArgsConstructor
public class PostTargetDataModel {
    public final String id;
    public final String scheduledPostId;
    public final String platformConnectionId;
    public final PostTargetType targetType;
}
