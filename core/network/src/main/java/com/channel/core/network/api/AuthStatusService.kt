package com.channel.core.network.api

import com.channel.core.network.model.AuthStatusResponse
import retrofit2.Response
import retrofit2.http.GET

interface AuthStatusService {
    @GET("auth/status")
    suspend fun getStatus(): Response<AuthStatusResponse>
}
