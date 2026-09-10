package com.channel.core.upload.data

import kotlinx.serialization.Serializable

/** Mirrors UploadsService.presign()'s response shape exactly (uploads/uploads.service.ts). */
@Serializable
data class PresignResponse(
    val url: String,
    val fileName: String,
    val contentType: String,
    val sizeBytes: Long,
)
