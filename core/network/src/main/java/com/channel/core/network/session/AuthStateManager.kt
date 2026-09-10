package com.channel.core.network.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    /**
     * "Something changed that might affect the account-status gate" — e.g. email
     * just got verified — without an authState transition of its own. Whoever
     * derives destinations from authState (RootViewModel) should re-resolve on
     * this too; any feature can trigger it without depending on that ViewModel.
     */
    private val _refreshSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val refreshSignal: SharedFlow<Unit> = _refreshSignal.asSharedFlow()

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

    fun requestStatusRefresh() {
        _refreshSignal.tryEmit(Unit)
    }

    suspend fun logout() {
        tokenManager.clear()
        setLoggedOut()
    }
}
