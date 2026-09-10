package com.channel.feature.onboarding.data

import kotlinx.serialization.Serializable

/** Mirrors channel-service's SubmitAudioDto exactly (onboarding/onboarding.dto.ts). */
@Serializable
data class SubmitAudioRequest(val audioUrl: String)
