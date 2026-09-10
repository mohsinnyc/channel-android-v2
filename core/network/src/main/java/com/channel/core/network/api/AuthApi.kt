package com.channel.core.network.api

import com.channel.core.network.model.AuthStatusResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthApi @Inject constructor(
    private val service: AuthStatusService
) {
    suspend fun getStatus(): ApiResult<AuthStatusResponse> = safeApiCall { service.getStatus() }
}
