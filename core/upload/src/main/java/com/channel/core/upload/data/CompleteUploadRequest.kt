package com.channel.core.upload.data

import kotlinx.serialization.Serializable

/** Mirrors channel-service's CompleteUploadDto exactly (uploads/uploads.dto.ts). */
@Serializable
data class CompleteUploadRequest(
    val fileName: String,
)
