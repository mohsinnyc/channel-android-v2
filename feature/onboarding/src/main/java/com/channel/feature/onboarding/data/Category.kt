package com.channel.feature.onboarding.data

import kotlinx.serialization.Serializable

/** Mirrors channel-service's listInterests() category shape exactly (onboarding/onboarding.service.ts). */
@Serializable
data class Category(
    val id: String,
    val label: String,
)
