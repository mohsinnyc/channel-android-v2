package com.channel.feature.profile.navigation

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

        // Playback deliberately keeps going if the user switches tabs or
        // navigates away - it's driven by the app-wide AudioPlayer singleton,
        // not this screen's lifecycle. See core:audio for the mediaId scheme
        // that keeps the same content's playing state consistent everywhere
        // it's rendered.
        ProfileScreen(
            uiState = uiState,
            onRetry = viewModel::retry,
            onToggleVoiceBioPlayback = viewModel::toggleVoiceBioPlayback,
            onToggleAudio = viewModel::toggleAudio,
            onLogout = viewModel::logout,
        )
    }
}
