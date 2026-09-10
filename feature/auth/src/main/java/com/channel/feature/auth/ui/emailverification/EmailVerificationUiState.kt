package com.channel.feature.auth.ui.emailverification

import com.channel.core.network.api.NetworkError
import com.channel.feature.auth.validation.VerificationCodeValidationError

data class EmailVerificationUiState(
    val code: String = "",
    val codeError: VerificationCodeValidationError? = null,
    val isVerifying: Boolean = false,
    val verifyError: NetworkError? = null,
    val isResending: Boolean = false,
    val resendError: NetworkError? = null,
    val resendConfirmationVisible: Boolean = false,
) {
    val canVerify: Boolean get() = !isVerifying && code.isNotBlank()
}
