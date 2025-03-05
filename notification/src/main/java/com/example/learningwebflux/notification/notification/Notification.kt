package com.example.learningwebflux.notification.notification

import com.fasterxml.jackson.databind.JsonNode
import java.time.OffsetDateTime

internal class Notification(
    val id: String,
    val userId: String,
    val type: String,
    val createdAt: OffsetDateTime,
    message: String,
    details: JsonNode?,
    read: Boolean,
) {
    fun changeMessage(newMessage: String) {
        _message = newMessage
    }

    fun markAsRead() {
        _read = true
    }

    val message get() = _message

    val details get() = _details

    val read get() = _read


    private var _message: String = message
    private var _details: JsonNode? = details
    private var _read: Boolean = read


    companion object {
        fun create(
            id: String,
            userId: String,
            type: String,
            message: String,
            details: JsonNode?,
        ): Notification {
            return Notification(
                id = id,
                userId = userId,
                type = type,
                createdAt = OffsetDateTime.now(),
                message = message,
                details = details,
                read = true,
            )
        }
    }
}