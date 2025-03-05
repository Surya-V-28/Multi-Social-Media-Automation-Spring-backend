package com.example.learningwebflux.medialibrary.user

import org.springframework.data.relational.core.mapping.Table

@Table("user")
internal data class User(val id: String)