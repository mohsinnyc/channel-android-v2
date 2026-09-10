package com.channel.feature.auth.navigation

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordDestination(val email: String)
