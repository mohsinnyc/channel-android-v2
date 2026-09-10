package com.channel.feature.auth.ui.forgotpassword

import com.channel.core.network.api.NetworkError
import com.channel.feature.auth.validation.EmailValidationError

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: EmailValidationError? = null,
    val isSubmitting: Boolean = false,
    val submitError: NetworkError? = null,
) {
    val canSubmit: Boolean get() = !isSubmitting && email.isNotBlank()
}
