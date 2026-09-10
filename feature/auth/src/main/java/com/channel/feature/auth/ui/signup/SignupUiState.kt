package com.channel.feature.auth.ui.signup

import com.channel.core.network.api.NetworkError
import com.channel.feature.auth.validation.EmailValidationError
import com.channel.feature.auth.validation.PasswordValidationError
import com.channel.feature.auth.validation.UsernameValidationError

data class SignupUiState(
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val usernameError: UsernameValidationError? = null,
    val passwordError: PasswordValidationError? = null,
    val emailError: EmailValidationError? = null,
    val isSubmitting: Boolean = false,
    val submitError: NetworkError? = null,
) {
    val canSubmit: Boolean get() = !isSubmitting && username.isNotBlank() && password.isNotBlank() && email.isNotBlank()
}
