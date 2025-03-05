package com.example.learningwebflux.medialibrary.restapi.models

import com.example.learningwebflux.medialibrary.media.ImageMedia
import com.example.learningwebflux.medialibrary.media.Media
import com.example.learningwebflux.medialibrary.media.VideoMedia
import com.example.learningwebflux.medialibrary.media.MediaInfo as DomainMediaInfo

internal data class MediaNetworkModel(
    val mediaInfo: MediaInfo,
    val typeDetails: MediaTypeDetails,
) {
    companion object {
        fun fromDomainModel(media: Media, url: String): MediaNetworkModel {
            return MediaNetworkModel(
                mediaInfo = MediaInfo.fromDomainModel(media.mediaInfo, url),
                typeDetails = when (media.mediaInfo.mimeType.type) {
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
        val mimeType: String,
        val url: String,
        val size: Long,
    ) {
        companion object {
            fun fromDomainModel(domainModel: DomainMediaInfo, url: String): MediaInfo {
                return MediaInfo(
                    keyId = domainModel.id,
                    name = domainModel.name,
                    mimeType = domainModel.mimeType.toString(),
                    url = url,
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
        companion object {
            fun fromDomainModel(domainModel: ImageMedia): ImageMediaTypeDetails {
                return ImageMediaTypeDetails(width = domainModel.width, height = domainModel.height)
            }
        }
    }

    data class VideoMediaTypeDetails(
        val duration: Long,
    ) : MediaTypeDetails {
        companion object {
            fun fromDomainModel(domainModel: VideoMedia): VideoMediaTypeDetails {
                return VideoMediaTypeDetails(duration = domainModel.duration.inWholeMilliseconds)
            }
        }
    }
}