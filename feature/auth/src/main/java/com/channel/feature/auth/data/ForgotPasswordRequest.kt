package com.channel.feature.auth.data

import kotlinx.serialization.Serializable

/** Mirrors channel-service's ForgotPasswordDto exactly (auth/auth.dto.ts). */
@Serializable
data class ForgotPasswordRequest(
    val email: String,
)
