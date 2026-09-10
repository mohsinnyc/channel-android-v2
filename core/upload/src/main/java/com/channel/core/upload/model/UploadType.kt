package com.channel.core.upload.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Mirrors channel-service's PresignUploadDto.type exactly (uploads/uploads.dto.ts). */
@Serializable
enum class UploadType {
    @SerialName("image") IMAGE,
    @SerialName("audio") AUDIO,
}
