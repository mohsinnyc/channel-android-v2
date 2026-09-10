package com.channel.feature.onboarding.data

import kotlinx.serialization.Serializable

/** Mirrors channel-service's SubmitInterestsDto exactly (onboarding/onboarding.dto.ts) — an empty list is valid. */
@Serializable
data class SubmitInterestsRequest(val categoryIds: List<String>)
