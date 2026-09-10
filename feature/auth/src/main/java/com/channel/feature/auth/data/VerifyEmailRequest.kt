package com.channel.feature.auth.data

import kotlinx.serialization.Serializable

/** Mirrors channel-service's VerifyEmailDto exactly (auth/auth.dto.ts) — code matches /^\d{6}$/. */
@Serializable
data class VerifyEmailRequest(
    val code: String,
)
