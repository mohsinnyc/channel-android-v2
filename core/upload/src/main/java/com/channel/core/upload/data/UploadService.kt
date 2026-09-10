package com.channel.core.upload.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UploadService {
    @POST("preSignUrl")
    suspend fun presign(@Body request: PresignRequest): Response<PresignResponse>

    @POST("uploads/complete")
    suspend fun complete(@Body request: CompleteUploadRequest): Response<ResponseBody>
}
