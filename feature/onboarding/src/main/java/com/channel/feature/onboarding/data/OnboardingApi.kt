package com.channel.feature.onboarding.data

import com.channel.core.network.api.ApiResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingApi @Inject constructor(
    private val client: HttpClient
) {
    suspend fun submitImage(request: SubmitImageRequest): ApiResult<Unit> = post("onboarding/image", request)

    suspend fun submitBio(request: SubmitBioRequest): ApiResult<Unit> = post("onboarding/bio", request)

    suspend fun submitAudio(request: SubmitAudioRequest): ApiResult<Unit> = post("onboarding/audio", request)

    suspend fun submitInterests(request: SubmitInterestsRequest): ApiResult<Unit> = post("onboarding/interests", request)

    suspend fun listInterests(): ApiResult<InterestsResponse> = runCatchingRequest { client.get("onboarding/interests") }

    private suspend inline fun <reified Req, reified Res> post(path: String, body: Req): ApiResult<Res> =
        runCatchingRequest {
            client.post(path) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        }

    private suspend inline fun <reified Res> runCatchingRequest(request: () -> HttpResponse): ApiResult<Res> {
        return try {
            val response = request()
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
