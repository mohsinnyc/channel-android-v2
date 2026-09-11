package com.channel.feature.profile.data

import com.channel.core.network.api.ApiResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileApi: ProfileApi
) {
    suspend fun getMine(): ApiResult<ProfileResponse> =
        if (USE_MOCK_DATA) ApiResult.Success(MockProfileData.sample()) else profileApi.getMine()

    private companion object {
        // TEMPORARY — flip back to false once there's a real followed/posting
        // account to look at. See MockProfileData's own kdoc.
        const val USE_MOCK_DATA = true
    }
}
