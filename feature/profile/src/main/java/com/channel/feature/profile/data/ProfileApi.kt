package com.channel.feature.profile.data

import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileApi @Inject constructor(
    private val service: ProfileService
) {
    suspend fun getMine(): ApiResult<ProfileResponse> = safeApiCall { service.getMine() }
}
