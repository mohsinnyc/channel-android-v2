package com.channel.feature.auth.ui.emailverification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EmailVerificationDestination() {
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
