package com.channel.feature.auth.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.channel.feature.auth.ui.resetpassword.ResetPasswordEvent
import com.channel.feature.auth.ui.resetpassword.ResetPasswordScreen
import com.channel.feature.auth.ui.resetpassword.ResetPasswordViewModel

fun NavGraphBuilder.resetPasswordDestination(navController: NavHostController) {
    composable<ResetPasswordDestination> {
        val viewModel: ResetPasswordViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(viewModel) {
            viewModel.events.collect { event ->
                when (event) {
                    ResetPasswordEvent.PasswordReset ->
                        navController.navigate(LoginDestination) {
                            popUpTo<LoginDestination> { inclusive = true }
                            launchSingleTop = true
                        }
                }
            }
        }

        ResetPasswordScreen(
            uiState = uiState,
            onCodeChanged = viewModel::onCodeChanged,
            onNewPasswordChanged = viewModel::onNewPasswordChanged,
            onSubmit = viewModel::submit,
            onUseDifferentEmail = {
                navController.navigate(ForgotPasswordDestination) {
                    popUpTo<LoginDestination>()
                }
            },
        )
    }
}
