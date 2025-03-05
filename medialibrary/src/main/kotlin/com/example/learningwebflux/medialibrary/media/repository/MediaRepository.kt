package com.example.learningwebflux.medialibrary.media.repository

import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate

import com.example.learningwebflux.medialibrary.media.Media
import com.example.learningwebflux.medialibrary.media.ImageMedia
import com.example.learningwebflux.medialibrary.media.VideoMedia
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
internal class MediaRepository(@Qualifier("mediaLibraryEntityTemplate") private val entityTemplate: R2dbcEntityTemplate) {
	@Transactional
    suspend fun save(media: Media) {
        val entityCount = entityTemplate.select(MediaDataModel::class.java)
            .matching(query(where("id").`is`(media.mediaInfo.id)))
            .count()
            .awaitSingle()

		val mediaDataModel = MediaDataModel.fromDomainModel(media)
        if (entityCount == 0L) {
            entityTemplate.insert(mediaDataModel).awaitSingle()

			when (media) {
				is ImageMedia -> {
					val dataModel = ImageMediaDataModel.fromDomainModel(media)
					entityTemplate.insert(dataModel).awaitSingle()
				}

				is VideoMedia -> {
					val dataModel = VideoMediaDataModel.fromDomainModel(media)
					entityTemplate.insert(dataModel).awaitSingle()
				}
			}
        }
        else {
            entityTemplate.update(mediaDataModel).awaitSingle()

			when (media) {
				is ImageMedia -> {
					val dataModel = ImageMediaDataModel.fromDomainModel(media)
					entityTemplate.update(dataModel).awaitSingle()
				}
				is VideoMedia -> {
					val dataModel = VideoMediaDataModel.fromDomainModel(media)
					entityTemplate.update(dataModel).awaitSingle()
				}
			}
        }
    }

	suspend fun getWithId(id: String): Media? {
		val dataModel = entityTemplate.select(MediaJoinViewDataModel::class.java)
			.matching(query(where("id").`is`(id)))
			.one()
			.awaitSingleOrNull() ?: return null

		return dataModel.toDomainModel()
	}

	suspend fun getWithIds(ids: List<String>): List<Media> {
		val queryCriteria = where("id").`is`(ids[0])
		for (id in ids.subList(1, ids.size)) {
			 queryCriteria.or(where("id").`is`(id))
		}

		return entityTemplate.select(MediaJoinViewDataModel::class.java)
			.all()
			.asFlow()
			.toList()
			.map { mediaDataModel -> mediaDataModel.toDomainModel() }

	}

	suspend fun getOfUser(userId: String): List<Media> {
		val count = entityTemplate.select(MediaJoinViewDataModel::class.java)
			.matching(query( where("user_id").`is`(userId) ))
			.all()
			.count()
			.block()!!

		println("CustomLog: Count = $count")

		return entityTemplate.select(MediaJoinViewDataModel::class.java)
			.matching(query( where("user_id").`is`(userId) ))
			.all()
			.collectList()
			.awaitSingle()
			.map { it.toDomainModel() }
	}

	@Transactional
	suspend fun delete(id: String) {
		entityTemplate.delete(query(where("media_id").`is`(id)), ImageMediaDataModel::class.java).awaitSingle()
		entityTemplate.delete(query(where("media_id").`is`(id)), VideoMediaDataModel::class.java).awaitSingle()
		entityTemplate.delete(query(where("id").`is`(id)), MediaDataModel::class.java).awaitSingle()
	}
}
