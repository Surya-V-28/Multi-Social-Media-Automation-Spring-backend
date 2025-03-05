package com.example.learningwebflux.postanalytics.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
internal data class User(@Id public val id: String)