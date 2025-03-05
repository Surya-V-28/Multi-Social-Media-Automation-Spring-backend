package com.example.learningwebflux.post.scheduledpost.repository.r2dbc.datamodel;

import lombok.AllArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("facebook_page_post_target_details")
@AllArgsConstructor
public class FacebookPagePostTargetDetailsDataModel {
    public final String scheduledPostId;
    public final String postTargetId;
    public final String pageId;
}
