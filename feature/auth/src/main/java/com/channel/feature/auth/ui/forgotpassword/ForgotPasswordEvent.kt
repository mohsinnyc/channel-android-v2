package com.channel.feature.auth.ui.forgotpassword

/** One-shot signal, not state — avoids re-firing navigation on recomposition/rotation. */
sealed interface ForgotPasswordEvent {
    data class CodeSent(val email: String) : ForgotPasswordEvent
}
