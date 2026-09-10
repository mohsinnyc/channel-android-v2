package com.channel.feature.auth.ui.resetpassword

import com.channel.core.network.api.NetworkError
import com.channel.feature.auth.validation.PasswordValidationError
import com.channel.feature.auth.validation.VerificationCodeValidationError

data class ResetPasswordUiState(
    val email: String = "",
    val code: String = "",
    val newPassword: String = "",
    val codeError: VerificationCodeValidationError? = null,
    val passwordError: PasswordValidationError? = null,
    val isSubmitting: Boolean = false,
    val submitError: NetworkError? = null,
) {
    val canSubmit: Boolean get() = !isSubmitting && code.isNotBlank() && newPassword.isNotBlank()
}
