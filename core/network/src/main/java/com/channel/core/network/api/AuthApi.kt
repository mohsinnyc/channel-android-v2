package com.channel.core.network.api

import com.channel.core.network.model.AuthStatusResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthApi @Inject constructor(
    private val client: HttpClient
) {
    suspend fun getStatus(): ApiResult<AuthStatusResponse> = safeCall {
        client.get("auth/status")
    }

    private suspend inline fun <reified T> safeCall(request: () -> HttpResponse): ApiResult<T> {
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
