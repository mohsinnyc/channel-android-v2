package com.channel.feature.auth.data

import kotlinx.serialization.Serializable

/** Mirrors channel-service's SignUpDto exactly (auth/auth.dto.ts). */
@Serializable
data class SignUpRequest(
    val username: String,
    val password: String,
    val email: String,
)
