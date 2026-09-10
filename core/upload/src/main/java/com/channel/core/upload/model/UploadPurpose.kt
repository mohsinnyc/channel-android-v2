package com.channel.core.upload.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Mirrors channel-service's PresignUploadDto.purpose exactly (uploads/uploads.dto.ts). */
@Serializable
enum class UploadPurpose {
    @SerialName("profile") PROFILE,
    @SerialName("post") POST,
    @SerialName("comment") COMMENT,
    @SerialName("temp") TEMP,
}
