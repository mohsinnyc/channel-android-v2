package com.channel.feature.auth.data

import kotlinx.serialization.Serializable

/** Mirrors channel-service's ResetPasswordDto exactly (auth/auth.dto.ts). */
@Serializable
data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String,
)
