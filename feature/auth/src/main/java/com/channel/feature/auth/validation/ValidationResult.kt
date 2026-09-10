package com.channel.feature.auth.validation

/** Carries *what* is wrong, never copy — the composable resolves [Invalid.reason] via stringResource. */
sealed interface ValidationResult<out E> {
    data object Valid : ValidationResult<Nothing>
    data class Invalid<E>(val reason: E) : ValidationResult<E>
}
