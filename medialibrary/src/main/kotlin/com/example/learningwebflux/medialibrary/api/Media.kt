package com.example.learningwebflux.medialibrary.api

import com.example.learningwebflux.medialibrary.media.ImageMedia
import com.example.learningwebflux.medialibrary.media.VideoMedia
import org.springframework.util.MimeType
import kotlin.time.Duration
import com.example.learningwebflux.medialibrary.media.Media as DomainMedia
import com.example.learningwebflux.medialibrary.media.MediaInfo as DomainMediaInfo

data class Media(
    val mediaInfo: MediaInfo,
    val mediaTypeDetails: MediaTypeDetails,
) {
    internal companion object {
        fun fromDomainModel(media: DomainMedia): Media {
            return Media(
                mediaInfo = MediaInfo.fromDomainModel(media.mediaInfo),
                mediaTypeDetails = when (media.mediaInfo.mimeType.type) {
                    "image" -> ImageMediaTypeDetails.fromDomainModel(media as ImageMedia)
                    "video" -> VideoMediaTypeDetails.fromDomainModel(media as VideoMedia)
                    else -> throw Error("Cannot map from mimeType to MediaTypeDetails")
                },
            )
        }
    }

    data class MediaInfo(
        val keyId: String,
        val name: String,
        val mimeType: MimeType,
        val size: Long,
    ) {
        internal companion object {
            fun fromDomainModel(domainModel: DomainMediaInfo): MediaInfo {
                return MediaInfo(
                    keyId = domainModel.id,
                    name = domainModel.name,
                    mimeType = domainModel.mimeType,
                    size = domainModel.size,
                )
            }
        }
    }

    sealed interface MediaTypeDetails

    data class ImageMediaTypeDetails(
        val width: Int,
        val height: Int,
    ): MediaTypeDetails {
        internal companion object {
            fun fromDomainModel(domainModel: ImageMedia): ImageMediaTypeDetails {
                return ImageMediaTypeDetails(width = domainModel.width, height = domainModel.height)
            }
        }
    }

    data class VideoMediaTypeDetails(
        val duration: Duration,
    ) : MediaTypeDetails {
        internal companion object {
            fun fromDomainModel(domainModel: VideoMedia): VideoMediaTypeDetails {
                return VideoMediaTypeDetails(duration = domainModel.duration)
            }
        }
    }
}
