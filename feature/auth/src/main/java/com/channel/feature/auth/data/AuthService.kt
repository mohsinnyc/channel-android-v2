package com.channel.feature.auth.data

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ResponseBody>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResponseBody>

    @POST("auth/email-verification/request")
    suspend fun requestEmailVerification(@Body body: RequestBody): Response<ResponseBody>

    @POST("auth/email-verification/verify")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<ResponseBody>
}
