package com.channel.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.channel.core.network.model.OnboardingState
import com.channel.feature.onboarding.navigation.AudioDestination
import com.channel.feature.onboarding.navigation.BioDestination
import com.channel.feature.onboarding.navigation.InterestsDestination
import com.channel.feature.onboarding.navigation.ProfileImageDestination
import com.channel.feature.onboarding.navigation.onboardingNavGraph

/** Resumes at whatever step /auth/status says the account is actually on. */
@Composable
fun OnboardingDestinationHost(startState: OnboardingState) {
    val navController = rememberNavController()
    val startDestination = when (startState) {
        // NewUser is never actually assigned by the backend today (the entity
        // defaults new users straight to ProfileImage) — treated the same here
        // defensively rather than inventing an unrequested intro screen for it.
        OnboardingState.NewUser, OnboardingState.ProfileImage -> ProfileImageDestination
        OnboardingState.Bio -> BioDestination
        OnboardingState.Audio -> AudioDestination
        OnboardingState.Interests, OnboardingState.Complete -> InterestsDestination
    }

    NavHost(navController = navController, startDestination = startDestination) {
        onboardingNavGraph(navController)
    }
}
