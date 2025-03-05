package com.example.learningwebflux.post.scheduledpost.repository.r2dbc.datamodel;

import lombok.AllArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.http.MediaType;

public record PostMediaDataModel(
    String id,
    String fileId
) { }
