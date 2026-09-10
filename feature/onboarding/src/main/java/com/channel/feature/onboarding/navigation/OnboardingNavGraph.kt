package com.channel.feature.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

fun NavGraphBuilder.onboardingNavGraph(navController: NavHostController) {
    profileImageDestination(navController)
    bioDestination(navController)
    audioDestination(navController)
    interestsDestination()
}
