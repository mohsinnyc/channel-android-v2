package com.channel.feature.onboarding.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.channel.feature.onboarding.ui.interests.InterestsScreen
import com.channel.feature.onboarding.ui.interests.InterestsViewModel

fun NavGraphBuilder.interestsDestination() {
    composable<InterestsDestination> {
        val viewModel: InterestsViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()

        InterestsScreen(
            uiState = uiState,
            onToggleCategory = viewModel::toggleCategory,
            onFinish = viewModel::finish,
        )
    }
}
