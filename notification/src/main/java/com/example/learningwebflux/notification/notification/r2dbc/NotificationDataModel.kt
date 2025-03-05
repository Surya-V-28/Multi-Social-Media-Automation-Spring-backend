package com.example.learningwebflux.notification.notification.r2dbc

import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime

@Table("notification")
internal data class NotificationDataModel(
    @Id
    val id: String,
    val userId: String,
    val message: String,
    val createdAt: OffsetDateTime,
    val type: String,
    val details: Json?,
    val read: Boolean,
)