package com.channel.feature.auth.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.channel.feature.auth.ui.signup.SignupScreen
import com.channel.feature.auth.ui.signup.SignupViewModel

fun NavGraphBuilder.signupDestination(navController: NavHostController) {
    composable<SignupDestination> {
        val viewModel: SignupViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()

        SignupScreen(
            uiState = uiState,
            onUsernameChanged = viewModel::onUsernameChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onEmailChanged = viewModel::onEmailChanged,
            onSubmit = viewModel::submit,
            onNavigateToLogin = { navController.navigate(LoginDestination) },
        )
    }
}
