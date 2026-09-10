package com.channel.feature.auth.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.channel.feature.auth.ui.forgotpassword.ForgotPasswordEvent
import com.channel.feature.auth.ui.forgotpassword.ForgotPasswordScreen
import com.channel.feature.auth.ui.forgotpassword.ForgotPasswordViewModel

fun NavGraphBuilder.forgotPasswordDestination(navController: NavHostController) {
    composable<ForgotPasswordDestination> {
        val viewModel: ForgotPasswordViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(viewModel) {
            viewModel.events.collect { event ->
                when (event) {
                    is ForgotPasswordEvent.CodeSent ->
                        navController.navigate(ResetPasswordDestination(event.email))
                }
            }
        }

        ForgotPasswordScreen(
            uiState = uiState,
            onEmailChanged = viewModel::onEmailChanged,
            onSubmit = viewModel::submit,
            onNavigateToLogin = { navController.navigate(LoginDestination) },
        )
    }
}
