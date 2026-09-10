package com.channel.feature.auth.ui.resetpassword

sealed interface ResetPasswordEvent {
    data object PasswordReset : ResetPasswordEvent
}
