package com.channel.core.upload.data

import com.channel.core.network.api.ApiResult
import com.channel.core.network.di.RawHttpClient
import com.channel.core.upload.model.UploadPurpose
import com.channel.core.upload.model.UploadType
import com.channel.core.upload.model.contentType
import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Presign -> PUT to S3 -> confirm completion. The backend rejects any post,
 * comment, or profile edit that references media stuck PENDING, so the
 * complete() call is never optional.
 */
@Singleton
class UploadRepository @Inject constructor(
    private val uploadApi: UploadApi,
    @RawHttpClient private val rawClient: HttpClient,
) {
    suspend fun uploadFile(file: File, type: UploadType, purpose: UploadPurpose): ApiResult<String> {
        val presign = when (val result = uploadApi.presign(PresignRequest(type, purpose, file.length()))) {
            is ApiResult.Success -> result.data
            is ApiResult.Error -> return result
            is ApiResult.Exception -> return result
        }

        val putSucceeded = try {
            val response = rawClient.put(presign.url) {
                contentType(ContentType.parse(type.contentType))
                setBody(file.readBytes())
            }
            response.status.isSuccess()
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            return ApiResult.Exception(e)
        }
        if (!putSucceeded) return ApiResult.Error(502, "Upload to storage failed")

        return when (val result = uploadApi.complete(CompleteUploadRequest(presign.fileName))) {
            is ApiResult.Success -> ApiResult.Success(presign.fileName)
            is ApiResult.Error -> result
            is ApiResult.Exception -> result
        }
    }
}
