package com.example.learningwebflux.medialibrary.restapi.addmedia

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.springframework.util.MimeType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@JsonDeserialize(using = AddMediaRequestBodyDeserializer::class)
internal data class AddMediaRequestBody(
    val mediaInfo: MediaInfo,
    val mediaTypeDetails: MediaTypeDetails,
) {
    data class MediaInfo(
        val id: String,
        val name: String,
        val mimeType: MimeType,
        val size: Long,
    )

    sealed interface MediaTypeDetails

    data class ImageMediaTypeDetails(
        val width: Int,
        val height: Int,
    ) : MediaTypeDetails

    data class VideoMediaTypeDetails(
        val duration: Duration,
    ) : MediaTypeDetails
}

internal class AddMediaRequestBodyDeserializer : StdDeserializer<AddMediaRequestBody>(null as Class<Any>?) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): AddMediaRequestBody {
        val jsonNode: JsonNode = p.readValueAsTree()

        val mediaInfoJsonNode = jsonNode.get("mediaInfo")
        val mediaInfo = AddMediaRequestBody.MediaInfo(
            id = mediaInfoJsonNode.get("id").asText(),
            name = mediaInfoJsonNode.get("name").asText(),
            mimeType = MimeType.valueOf(mediaInfoJsonNode.get("mimeType").asText()),
            size = mediaInfoJsonNode.get("size").asLong(),
        )

        return AddMediaRequestBody(
            mediaInfo = mediaInfo,
            mediaTypeDetails = jsonNode.get("mediaTypeDetails")
                .let {
                    val mimeType = MimeType.valueOf(mediaInfoJsonNode.get("mimeType").asText()).type
                    return@let when (mediaInfo.mimeType.type) {
                        "image" -> AddMediaRequestBody.ImageMediaTypeDetails(
                            width = it.get("width").asInt(),
                            height = it.get("height").asInt(),
                        )

                        "video" -> AddMediaRequestBody.VideoMediaTypeDetails(
                            duration = it.get("duration").asLong().milliseconds
                        )

                        else -> throw Error("Cannot happen")
                    }
                },
        )
    }
}