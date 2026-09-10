package com.channel.feature.auth.validation

/** Mirrors channel-service's @IsEmail + MaxLength(320) constraint (auth/auth.dto.ts). */
object EmailValidator {
    private val emailPattern = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

    fun validate(email: String): ValidationResult<EmailValidationError> = when {
        email.isBlank() -> ValidationResult.Invalid(EmailValidationError.BLANK)
        email.length > 320 -> ValidationResult.Invalid(EmailValidationError.TOO_LONG)
        !emailPattern.matches(email) -> ValidationResult.Invalid(EmailValidationError.INVALID)
        else -> ValidationResult.Valid
    }
}
