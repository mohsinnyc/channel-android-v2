package com.channel.feature.auth.data

import kotlinx.serialization.Serializable

/** Mirrors channel-service's AuthResponse exactly (auth/auth.dto.ts) — note authToken, not accessToken. */
@Serializable
data class AuthResponse(
    val userId: String,
    val authToken: String,
    val refreshToken: String,
)
