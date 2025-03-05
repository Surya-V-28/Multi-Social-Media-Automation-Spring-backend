package com.example.learningwebflux.medialibrary.media.repository

import com.example.learningwebflux.medialibrary.media.Media
import com.example.learningwebflux.medialibrary.media.VideoMedia
import com.example.learningwebflux.medialibrary.media.ImageMedia
import com.example.learningwebflux.medialibrary.media.MediaInfo
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.util.MimeType
import kotlin.time.Duration.Companion.milliseconds

@Table("media")
internal data class MediaDataModel(
    @Id
    val id: String,
    val userId: String,
	val name: String,
    val mimeType: String,
    val size: Long,
) {
	companion object {
		fun fromDomainModel(domainModel: Media): MediaDataModel {
			return MediaDataModel(
				id = domainModel.mediaInfo.id,
				userId = domainModel.mediaInfo.userId,
				name = domainModel.mediaInfo.name,
				mimeType = domainModel.mediaInfo.mimeType.toString(),
				size = domainModel.mediaInfo.size,
			)
		}
	}
}

sealed interface MediaTypeDataModel

@Table("image_media")
internal data class ImageMediaDataModel(
    val mediaId: String,
    val width: Int,
    val height: Int,
) : MediaTypeDataModel {
	companion object {
		fun fromDomainModel(domainModel: ImageMedia): ImageMediaDataModel {
			return ImageMediaDataModel(
				mediaId = domainModel.mediaInfo.id,
				width = domainModel.width,
				height = domainModel.height,
			)
		}
	}
}

@Table("video_media")
internal data class VideoMediaDataModel(
    val mediaId: String,
    val duration: Long,
) : MediaTypeDataModel {
	companion object {
		fun fromDomainModel(domainModel: VideoMedia): VideoMediaDataModel {
			return VideoMediaDataModel(
				mediaId = domainModel.mediaInfo.id,
				duration = domainModel.duration.inWholeMilliseconds,
			)
		}
	}
}

@Table("media_join_view")
internal data class MediaJoinViewDataModel(
	val id: String,
	val userId: String,
	val name: String,
	val mimeType: String,
	val size: Long,

	val width: Int?,
	val height: Int?,

	val duration: Long?,
) {
	fun toDomainModel(): Media {
		val mediaInfo = MediaInfo(
			id = id,
			userId = userId,
			name = name,
			mimeType = MimeType.valueOf(mimeType),
			size = size,
		)

		return when (mediaInfo.mimeType.type) {
			"image" -> ImageMedia(
				mediaInfo = mediaInfo,
				width = width!!,
				height = height!!,
			)

			"video" -> VideoMedia(
				mediaInfo = mediaInfo,
				duration = duration!!.milliseconds
			)

			else -> throw Error("Cannot map MediaJoinView data model with mimeType $mimeType to Domain Model")
		}
	}
}