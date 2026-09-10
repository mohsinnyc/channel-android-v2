package com.channel.core.network.session

sealed interface AuthState {
    data object Unknown : AuthState
    data object Authenticated : AuthState
    data object LoggedOut : AuthState
}
