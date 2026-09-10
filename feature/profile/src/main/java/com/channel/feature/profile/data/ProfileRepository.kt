package com.channel.feature.profile.data

import com.channel.core.network.api.ApiResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileApi: ProfileApi
) {
    suspend fun getMine(): ApiResult<ProfileResponse> = profileApi.getMine()
}
