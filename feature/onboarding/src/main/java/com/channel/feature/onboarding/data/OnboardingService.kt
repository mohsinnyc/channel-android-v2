package com.channel.feature.onboarding.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OnboardingService {
    @POST("onboarding/image")
    suspend fun submitImage(@Body request: SubmitImageRequest): Response<ResponseBody>

    @POST("onboarding/bio")
    suspend fun submitBio(@Body request: SubmitBioRequest): Response<ResponseBody>

    @POST("onboarding/audio")
    suspend fun submitAudio(@Body request: SubmitAudioRequest): Response<ResponseBody>

    @POST("onboarding/interests")
    suspend fun submitInterests(@Body request: SubmitInterestsRequest): Response<ResponseBody>

    @GET("onboarding/interests")
    suspend fun listInterests(): Response<InterestsResponse>
}
