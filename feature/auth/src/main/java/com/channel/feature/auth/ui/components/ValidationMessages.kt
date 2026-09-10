package com.channel.feature.auth.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.channel.feature.auth.R
import com.channel.feature.auth.validation.EmailValidationError
import com.channel.feature.auth.validation.PasswordValidationError
import com.channel.feature.auth.validation.UsernameValidationError
import com.channel.feature.auth.validation.VerificationCodeValidationError

@Composable
fun UsernameValidationError.message(): String = stringResource(
    when (this) {
        UsernameValidationError.BLANK -> R.string.error_username_blank
        UsernameValidationError.TOO_SHORT -> R.string.error_username_too_short
        UsernameValidationError.TOO_LONG -> R.string.error_username_too_long
        UsernameValidationError.INVALID_CHARACTERS -> R.string.error_username_invalid_characters
    }
)

@Composable
fun PasswordValidationError.message(): String = stringResource(
    when (this) {
        PasswordValidationError.BLANK -> R.string.error_password_blank
        PasswordValidationError.TOO_SHORT -> R.string.error_password_too_short
        PasswordValidationError.TOO_LONG -> R.string.error_password_too_long
    }
)

@Composable
fun EmailValidationError.message(): String = stringResource(
    when (this) {
        EmailValidationError.BLANK -> R.string.error_email_blank
        EmailValidationError.INVALID -> R.string.error_email_invalid
        EmailValidationError.TOO_LONG -> R.string.error_email_too_long
    }
)

@Composable
fun VerificationCodeValidationError.message(): String = stringResource(
    when (this) {
        VerificationCodeValidationError.BLANK -> R.string.error_code_blank
        VerificationCodeValidationError.INVALID_FORMAT -> R.string.error_code_invalid_format
    }
)
