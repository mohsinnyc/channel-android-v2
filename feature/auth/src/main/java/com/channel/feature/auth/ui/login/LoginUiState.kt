package com.channel.feature.auth.ui.login

import com.channel.core.network.api.NetworkError

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
    val submitError: NetworkError? = null,
) {
    val canSubmit: Boolean get() = !isSubmitting && username.isNotBlank() && password.isNotBlank()
}
