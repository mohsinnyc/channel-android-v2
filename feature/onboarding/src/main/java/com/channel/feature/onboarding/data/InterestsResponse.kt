package com.channel.feature.onboarding.data

import kotlinx.serialization.Serializable

@Serializable
data class InterestsResponse(val categories: List<Category>)
