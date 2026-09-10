package com.channel.feature.onboarding.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.channel.feature.onboarding.ui.OnboardingStepEvent
import com.channel.feature.onboarding.ui.bio.BioScreen
import com.channel.feature.onboarding.ui.bio.BioViewModel

fun NavGraphBuilder.bioDestination(navController: NavHostController) {
    composable<BioDestination> {
        val viewModel: BioViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(viewModel) {
            viewModel.events.collect { event ->
                when (event) {
                    OnboardingStepEvent.Continue -> navController.navigate(AudioDestination)
                }
            }
        }

        BioScreen(
            uiState = uiState,
            onBioChanged = viewModel::onBioChanged,
            onContinue = viewModel::submit,
            onSkip = viewModel::skip,
        )
    }
}
