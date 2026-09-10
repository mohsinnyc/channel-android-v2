package com.channel.feature.onboarding.navigation

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.channel.feature.onboarding.ui.OnboardingStepEvent
import com.channel.feature.onboarding.ui.audio.AudioScreen
import com.channel.feature.onboarding.ui.audio.AudioViewModel

fun NavGraphBuilder.audioDestination(navController: NavHostController) {
    composable<AudioDestination> {
        val viewModel: AudioViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()

        DisposableEffect(viewModel) {
            onDispose { viewModel.onLeaveScreen() }
        }

        LaunchedEffect(viewModel) {
            viewModel.events.collect { event ->
                when (event) {
                    OnboardingStepEvent.Continue -> navController.navigate(InterestsDestination)
                }
            }
        }

        AudioScreen(
            uiState = uiState,
            onStartRecording = viewModel::startRecording,
            onStopRecording = viewModel::stopRecording,
            onReRecord = viewModel::reRecord,
            onTogglePlayback = viewModel::togglePlayback,
            onContinue = viewModel::submit,
        )
    }
}
