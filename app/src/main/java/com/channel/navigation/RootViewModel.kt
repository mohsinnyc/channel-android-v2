package com.channel.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.AuthApi
import com.channel.core.network.api.toNetworkError
import com.channel.core.network.model.AuthStatusResponse
import com.channel.core.network.model.OnboardingState
import com.channel.core.network.session.AuthState
import com.channel.core.network.session.AuthStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val authStateManager: AuthStateManager,
    private val authApi: AuthApi
) : ViewModel() {

    val destination: StateFlow<AppDestination> = combine(
        authStateManager.authState,
        authStateManager.refreshSignal.onStart { emit(Unit) }
    ) { authState, _ -> authState }
        .flatMapLatest { authState ->
            when (authState) {
                AuthState.Unknown -> flow { emit(AppDestination.Loading) }
                AuthState.LoggedOut -> flow { emit(AppDestination.Auth) }
                AuthState.Authenticated -> flow {
                    emit(AppDestination.Loading)
                    emit(resolveAuthenticatedDestination())
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppDestination.Loading)

    /** Re-checks /auth/status — used by the Error destination's retry action. */
    fun retry() {
        authStateManager.requestStatusRefresh()
    }

    private suspend fun resolveAuthenticatedDestination(): AppDestination {
        return when (val result = authApi.getStatus()) {
            is ApiResult.Success -> result.data.toDestination()
            is ApiResult.Error -> {
                if (result.code == 401) {
                    authStateManager.setLoggedOut()
                    AppDestination.Auth
                } else {
                    AppDestination.Error(result.toNetworkError())
                }
            }
            is ApiResult.Exception -> AppDestination.Error(result.toNetworkError())
        }
    }

    private fun AuthStatusResponse.toDestination(): AppDestination = when {
        isBanned -> AppDestination.Banned
        !isEmailVerified -> AppDestination.EmailVerification
        onboardingState != OnboardingState.Complete -> AppDestination.Onboarding(onboardingState)
        else -> AppDestination.Main
    }
}
