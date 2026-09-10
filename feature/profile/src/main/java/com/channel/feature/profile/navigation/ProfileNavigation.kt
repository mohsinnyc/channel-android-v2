package com.channel.feature.profile.navigation

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.channel.feature.profile.ui.ProfileScreen
import com.channel.feature.profile.ui.ProfileViewModel

fun NavGraphBuilder.profileDestination() {
    composable<ProfileDestination> {
        val viewModel: ProfileViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()

        DisposableEffect(viewModel) {
            onDispose { viewModel.onLeaveScreen() }
        }

        ProfileScreen(
            uiState = uiState,
            onRetry = viewModel::retry,
            onToggleVoiceBioPlayback = viewModel::toggleVoiceBioPlayback,
            onTogglePostPlayback = viewModel::togglePostPlayback,
            onLogout = viewModel::logout,
        )
    }
}
