package com.channel.core.upload.data

import com.channel.core.upload.model.UploadPurpose
import com.channel.core.upload.model.UploadType
import kotlinx.serialization.Serializable

/** Mirrors channel-service's PresignUploadDto exactly (uploads/uploads.dto.ts). */
@Serializable
data class PresignRequest(
    val type: UploadType,
    val purpose: UploadPurpose,
    val sizeBytes: Long,
)
