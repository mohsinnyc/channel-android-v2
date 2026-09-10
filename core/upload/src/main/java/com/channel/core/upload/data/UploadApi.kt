package com.channel.core.upload.data

import com.channel.core.network.api.ApiResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadApi @Inject constructor(
    private val client: HttpClient
) {
    suspend fun presign(request: PresignRequest): ApiResult<PresignResponse> = post("preSignUrl", request)

    suspend fun complete(request: CompleteUploadRequest): ApiResult<Unit> = post("uploads/complete", request)

    private suspend inline fun <reified Req, reified Res> post(path: String, body: Req): ApiResult<Res> {
        return try {
            val response: HttpResponse = client.post(path) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body())
            } else {
                ApiResult.Error(response.status.value, response.status.description)
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: kotlin.Exception) {
            ApiResult.Exception(e)
        }
    }
}
