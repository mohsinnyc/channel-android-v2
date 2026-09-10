package com.channel.feature.onboarding.data

import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.safeApiCall
import com.channel.core.network.api.safeUnitApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingApi @Inject constructor(
    private val service: OnboardingService
) {
    suspend fun submitImage(request: SubmitImageRequest): ApiResult<Unit> =
        safeUnitApiCall { service.submitImage(request) }

    suspend fun submitBio(request: SubmitBioRequest): ApiResult<Unit> = safeUnitApiCall { service.submitBio(request) }

    suspend fun submitAudio(request: SubmitAudioRequest): ApiResult<Unit> =
        safeUnitApiCall { service.submitAudio(request) }

    suspend fun submitInterests(request: SubmitInterestsRequest): ApiResult<Unit> =
        safeUnitApiCall { service.submitInterests(request) }

    suspend fun listInterests(): ApiResult<InterestsResponse> = safeApiCall { service.listInterests() }
}
