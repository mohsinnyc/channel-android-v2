package com.channel.feature.onboarding.data

import kotlinx.serialization.Serializable

/** Mirrors channel-service's SubmitImageDto exactly (onboarding/onboarding.dto.ts). */
@Serializable
data class SubmitImageRequest(val imageUrl: String)
