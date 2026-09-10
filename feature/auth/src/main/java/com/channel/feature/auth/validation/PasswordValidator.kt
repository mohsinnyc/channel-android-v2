package com.channel.feature.auth.validation

/** Mirrors channel-service's password constraints exactly (auth/auth.dto.ts). */
object PasswordValidator {
    fun validate(password: String): ValidationResult<PasswordValidationError> = when {
        password.isBlank() -> ValidationResult.Invalid(PasswordValidationError.BLANK)
        password.length < 8 -> ValidationResult.Invalid(PasswordValidationError.TOO_SHORT)
        password.length > 128 -> ValidationResult.Invalid(PasswordValidationError.TOO_LONG)
        else -> ValidationResult.Valid
    }
}
