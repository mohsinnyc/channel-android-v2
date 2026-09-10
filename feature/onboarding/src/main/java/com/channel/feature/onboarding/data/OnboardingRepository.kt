package com.channel.feature.onboarding.data

import com.channel.core.network.api.ApiResult
import com.channel.core.network.session.AuthStateManager
import com.channel.core.upload.data.UploadRepository
import com.channel.core.upload.model.UploadPurpose
import com.channel.core.upload.model.UploadType
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepository @Inject constructor(
    private val onboardingApi: OnboardingApi,
    private val uploadRepository: UploadRepository,
    private val authStateManager: AuthStateManager,
) {
    suspend fun submitImage(file: File): ApiResult<Unit> {
        val fileName = when (val upload = uploadRepository.uploadFile(file, UploadType.IMAGE, UploadPurpose.PROFILE)) {
            is ApiResult.Success -> upload.data
            is ApiResult.Error -> return upload
            is ApiResult.Exception -> return upload
        }
        return onboardingApi.submitImage(SubmitImageRequest(fileName))
    }

    suspend fun submitBio(bio: String): ApiResult<Unit> =
        onboardingApi.submitBio(SubmitBioRequest(bio))

    suspend fun submitAudio(file: File): ApiResult<Unit> {
        val fileName = when (val upload = uploadRepository.uploadFile(file, UploadType.AUDIO, UploadPurpose.PROFILE)) {
            is ApiResult.Success -> upload.data
            is ApiResult.Error -> return upload
            is ApiResult.Exception -> return upload
        }
        return onboardingApi.submitAudio(SubmitAudioRequest(fileName))
    }

    suspend fun listInterests(): ApiResult<List<Category>> =
        when (val result = onboardingApi.listInterests()) {
            is ApiResult.Success -> ApiResult.Success(result.data.categories)
            is ApiResult.Error -> result
            is ApiResult.Exception -> result
        }

    /** The last step — this is the one that flips onboardingState to Complete server-side. */
    suspend fun submitInterests(categoryIds: List<String>): ApiResult<Unit> {
        val result = onboardingApi.submitInterests(SubmitInterestsRequest(categoryIds))
        if (result is ApiResult.Success) authStateManager.requestStatusRefresh()
        return result
    }
}
