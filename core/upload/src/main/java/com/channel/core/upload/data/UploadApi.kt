package com.channel.core.upload.data

import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.safeApiCall
import com.channel.core.network.api.safeUnitApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadApi @Inject constructor(
    private val service: UploadService
) {
    suspend fun presign(request: PresignRequest): ApiResult<PresignResponse> = safeApiCall { service.presign(request) }

    suspend fun complete(request: CompleteUploadRequest): ApiResult<Unit> = safeUnitApiCall { service.complete(request) }
}
