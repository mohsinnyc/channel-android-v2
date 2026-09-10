package com.channel.feature.onboarding.data

import kotlinx.serialization.Serializable

/** Mirrors channel-service's SubmitBioDto exactly (onboarding/onboarding.dto.ts). */
@Serializable
data class SubmitBioRequest(val bio: String)
