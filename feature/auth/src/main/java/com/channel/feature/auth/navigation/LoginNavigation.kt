package com.channel.feature.auth.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.channel.feature.auth.ui.login.LoginScreen
import com.channel.feature.auth.ui.login.LoginViewModel

fun NavGraphBuilder.loginDestination(navController: NavHostController) {
    composable<LoginDestination> {
        val viewModel: LoginViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LoginScreen(
            uiState = uiState,
            onUsernameChanged = viewModel::onUsernameChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onSubmit = viewModel::submit,
            onNavigateToSignup = { navController.navigate(SignupDestination) },
            onNavigateToForgotPassword = { navController.navigate(ForgotPasswordDestination) },
        )
    }
}
