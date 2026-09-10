package com.channel.feature.auth.data

import kotlinx.serialization.Serializable

/** Mirrors channel-service's LoginDto exactly (auth/auth.dto.ts). */
@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)
