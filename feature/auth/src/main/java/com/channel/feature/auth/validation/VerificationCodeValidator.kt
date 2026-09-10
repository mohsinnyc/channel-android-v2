package com.channel.feature.auth.validation

/** Mirrors channel-service's ResetPasswordDto code constraint exactly: /^\d{6}$/ (auth/auth.dto.ts). */
object VerificationCodeValidator {
    private val sixDigits = Regex("^\\d{6}$")

    fun validate(code: String): ValidationResult<VerificationCodeValidationError> = when {
        code.isBlank() -> ValidationResult.Invalid(VerificationCodeValidationError.BLANK)
        !sixDigits.matches(code) -> ValidationResult.Invalid(VerificationCodeValidationError.INVALID_FORMAT)
        else -> ValidationResult.Valid
    }
}
