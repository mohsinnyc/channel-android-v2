package com.channel.feature.auth.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.channel.feature.auth.ui.emailverification.EmailVerificationScreen
import com.channel.feature.auth.ui.emailverification.EmailVerificationViewModel

fun NavGraphBuilder.emailVerificationDestination() {
    composable<EmailVerificationDestination> {
        val viewModel: EmailVerificationViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()

        EmailVerificationScreen(
            uiState = uiState,
            onCodeChanged = viewModel::onCodeChanged,
            onVerify = viewModel::verify,
            onResendCode = viewModel::resendCode,
            onLogout = viewModel::logout,
        )
    }
}
