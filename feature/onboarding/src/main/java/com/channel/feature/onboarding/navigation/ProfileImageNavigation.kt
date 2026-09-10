package com.channel.feature.onboarding.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.channel.feature.onboarding.ui.OnboardingStepEvent
import com.channel.feature.onboarding.ui.profileimage.ProfileImageScreen
import com.channel.feature.onboarding.ui.profileimage.ProfileImageViewModel

fun NavGraphBuilder.profileImageDestination(navController: NavHostController) {
    composable<ProfileImageDestination> {
        val viewModel: ProfileImageViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(viewModel) {
            viewModel.events.collect { event ->
                when (event) {
                    OnboardingStepEvent.Continue -> navController.navigate(BioDestination)
                }
            }
        }

        ProfileImageScreen(
            uiState = uiState,
            onImagePicked = viewModel::onImagePicked,
            onContinue = viewModel::submit,
            onSkip = viewModel::skip,
        )
    }
}
