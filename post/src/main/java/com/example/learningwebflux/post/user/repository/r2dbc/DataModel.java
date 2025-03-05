package com.example.learningwebflux.post.user.repository.r2dbc;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("\"user\"")
@AllArgsConstructor
public class DataModel {
    @Id
    public final String id;
}
