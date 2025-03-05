package com.example.learningwebflux.medialibrary.media

import org.springframework.util.MimeType
import kotlin.time.Duration

internal sealed interface Media {
    val mediaInfo: MediaInfo
}

internal class MediaInfo(
    val id: String,
    val userId: String,
    val name: String,
    val mimeType: MimeType,
    val size: Long,
)

internal class ImageMedia(
    override val mediaInfo: MediaInfo,
    val width: Int,
    val height: Int,
) : Media

internal class VideoMedia(
    override val mediaInfo: MediaInfo,
    val duration: Duration,
) : Media