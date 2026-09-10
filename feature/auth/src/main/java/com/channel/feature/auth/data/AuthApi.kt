package com.channel.feature.auth.data

import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.safeApiCall
import com.channel.core.network.api.safeUnitApiCall
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.RequestBody.Companion.toRequestBody

@Singleton
class AuthApi @Inject constructor(
    private val service: AuthService
) {
    suspend fun signUp(request: SignUpRequest): ApiResult<AuthResponse> = safeApiCall { service.signUp(request) }

    suspend fun login(request: LoginRequest): ApiResult<AuthResponse> = safeApiCall { service.login(request) }

    suspend fun forgotPassword(request: ForgotPasswordRequest): ApiResult<Unit> =
        safeUnitApiCall { service.forgotPassword(request) }

    suspend fun resetPassword(request: ResetPasswordRequest): ApiResult<Unit> =
        safeUnitApiCall { service.resetPassword(request) }

    suspend fun requestEmailVerification(): ApiResult<Unit> =
        safeUnitApiCall { service.requestEmailVerification(ByteArray(0).toRequestBody(null)) }

    suspend fun verifyEmail(request: VerifyEmailRequest): ApiResult<Unit> =
        safeUnitApiCall { service.verifyEmail(request) }
}
