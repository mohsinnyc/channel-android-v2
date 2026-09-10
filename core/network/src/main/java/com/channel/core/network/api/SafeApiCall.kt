package com.channel.core.network.api

import kotlinx.coroutines.CancellationException
import okhttp3.ResponseBody
import retrofit2.Response

/** Wraps a Retrofit call that returns a real JSON body on success. */
suspend inline fun <T> safeApiCall(crossinline call: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = call()
        val body = response.body()
        when {
            response.isSuccessful && body != null -> ApiResult.Success(body)
            response.isSuccessful -> ApiResult.Error(response.code(), "Empty response body")
            else -> ApiResult.Error(response.code(), response.message())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ApiResult.Exception(e)
    }
}

/** Wraps a Retrofit call whose success body carries nothing worth parsing. */
suspend inline fun safeUnitApiCall(crossinline call: suspend () -> Response<ResponseBody>): ApiResult<Unit> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            ApiResult.Success(Unit)
        } else {
            ApiResult.Error(response.code(), response.message())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ApiResult.Exception(e)
    }
}
