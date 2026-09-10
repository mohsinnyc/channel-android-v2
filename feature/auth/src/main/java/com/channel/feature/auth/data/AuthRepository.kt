package com.channel.feature.auth.data

import com.channel.core.network.api.ApiResult
import com.channel.core.network.session.AuthStateManager
import com.channel.core.network.session.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val authStateManager: AuthStateManager,
) {
    suspend fun signUp(username: String, password: String, email: String): ApiResult<Unit> =
        authenticate(authApi.signUp(SignUpRequest(username, password, email)))

    suspend fun login(username: String, password: String): ApiResult<Unit> =
        authenticate(authApi.login(LoginRequest(username, password)))

    suspend fun forgotPassword(email: String): ApiResult<Unit> =
        authApi.forgotPassword(ForgotPasswordRequest(email))

    suspend fun resetPassword(email: String, code: String, newPassword: String): ApiResult<Unit> =
        authApi.resetPassword(ResetPasswordRequest(email, code, newPassword))

    suspend fun requestEmailVerification(): ApiResult<Unit> = authApi.requestEmailVerification()

    suspend fun verifyEmail(code: String): ApiResult<Unit> = authApi.verifyEmail(VerifyEmailRequest(code))

    /** Persists tokens and flips the app-wide session state on a successful signup/login. */
    private suspend fun authenticate(result: ApiResult<AuthResponse>): ApiResult<Unit> {
        if (result is ApiResult.Success) {
            tokenManager.saveTokens(result.data.authToken, result.data.refreshToken)
            authStateManager.setAuthenticated()
        }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(Unit)
            is ApiResult.Error -> result
            is ApiResult.Exception -> result
        }
    }
}
