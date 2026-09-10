package com.channel.feature.auth.validation

/** Mirrors channel-service's SignUpDto username constraints exactly (auth/auth.dto.ts). */
object UsernameValidator {
    private val allowedCharacters = Regex("^[A-Za-z0-9_.]+$")

    fun validate(username: String): ValidationResult<UsernameValidationError> = when {
        username.isBlank() -> ValidationResult.Invalid(UsernameValidationError.BLANK)
        username.length < 3 -> ValidationResult.Invalid(UsernameValidationError.TOO_SHORT)
        username.length > 50 -> ValidationResult.Invalid(UsernameValidationError.TOO_LONG)
        !allowedCharacters.matches(username) -> ValidationResult.Invalid(UsernameValidationError.INVALID_CHARACTERS)
        else -> ValidationResult.Valid
    }
}
