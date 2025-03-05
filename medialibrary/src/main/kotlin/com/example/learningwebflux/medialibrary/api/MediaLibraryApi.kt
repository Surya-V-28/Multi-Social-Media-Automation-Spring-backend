package com.example.learningwebflux.medialibrary.api

import com.example.learningwebflux.medialibrary.media.repository.MediaRepository
import org.springframework.stereotype.Component

@Component
class MediaLibraryApi internal constructor (private val mediaRepository: MediaRepository) {
    suspend fun getMedias(ids: List<String>): List<Media>  {
         return mediaRepository.getWithIds(ids)
             .map { Media.fromDomainModel(it) }
    }
}