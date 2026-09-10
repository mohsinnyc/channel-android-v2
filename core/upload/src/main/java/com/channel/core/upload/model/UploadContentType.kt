package com.channel.core.upload.model

/**
 * The backend fixes the Content-Type per type regardless of what's sent
 * (uploads.service.ts MEDIA_TYPES) — derive it from UploadType alone rather
 * than threading a separately-specified value through call sites, which is
 * exactly the kind of drift that caused every upload to fail with "does not
 * match the signed request" in the old app.
 */
val UploadType.contentType: String
    get() = when (this) {
        UploadType.IMAGE -> "image/jpeg"
        UploadType.AUDIO -> "audio/mp4"
    }

val UploadType.fileExtension: String
    get() = when (this) {
        UploadType.IMAGE -> "jpg"
        UploadType.AUDIO -> "m4a"
    }
