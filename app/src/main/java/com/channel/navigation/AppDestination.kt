package com.channel.navigation

import com.channel.core.network.api.NetworkError
import com.channel.core.network.model.OnboardingState

/**
 * The single source of truth for "what should be on screen at the top level."
 * MainActivity switches on this directly instead of using NavController to fake
 * top-level mode changes — each branch below owns its own NavHost.
 */
sealed interface AppDestination {
    /** Cold start: token presence known, but /auth/status hasn't resolved yet. */
    data object Loading : AppDestination
    data object Auth : AppDestination
    data object Banned : AppDestination
    data object EmailVerification : AppDestination
    data class Onboarding(val state: OnboardingState) : AppDestination
    data object Main : AppDestination
    data class Error(val reason: NetworkError) : AppDestination
}
