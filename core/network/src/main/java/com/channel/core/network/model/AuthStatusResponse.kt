package com.channel.core.network.model

import kotlinx.serialization.Serializable

/** Mirrors AuthService.status()'s response shape exactly (auth/auth.service.ts). */
@Serializable
data class AuthStatusResponse(
    val username: String,
    val email: String? = null,
    val isEmailVerified: Boolean,
    val isBanned: Boolean,
    val onboardingState: OnboardingState,
)
