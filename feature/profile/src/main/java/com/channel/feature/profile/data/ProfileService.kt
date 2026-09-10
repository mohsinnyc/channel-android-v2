package com.channel.feature.profile.data

import retrofit2.Response
import retrofit2.http.GET

interface ProfileService {
    @GET("profile")
    suspend fun getMine(): Response<ProfileResponse>
}
