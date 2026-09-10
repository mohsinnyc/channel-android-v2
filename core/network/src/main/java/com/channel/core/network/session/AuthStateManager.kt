package com.channel.core.network.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateManager @Inject constructor(
    private val tokenManager: TokenManager,
    externalScope: CoroutineScope
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)
    val authState: StateFlow<AuthState> = _authState

    init {
        externalScope.launch(Dispatchers.IO) {
            val token = tokenManager.getAccessTokenOnce()
            _authState.value = if (token.isNullOrEmpty()) AuthState.LoggedOut else AuthState.Authenticated
        }
    }

    fun setAuthenticated() {
        _authState.value = AuthState.Authenticated
    }

    fun setLoggedOut() {
        _authState.value = AuthState.LoggedOut
    }
}
