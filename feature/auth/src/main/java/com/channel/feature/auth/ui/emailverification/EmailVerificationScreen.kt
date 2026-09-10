package com.channel.feature.auth.ui.emailverification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.channel.core.designsystem.components.ChannelTextField
import com.channel.core.designsystem.components.PrimaryButton
import com.channel.core.designsystem.theme.Spacing
import com.channel.feature.auth.R
import com.channel.feature.auth.ui.components.message

@Composable
fun EmailVerificationScreen(
    uiState: EmailVerificationUiState,
    onCodeChanged: (String) -> Unit,
    onVerify: () -> Unit,
    onResendCode: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.email_verification_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(Spacing.s))
        Text(stringResource(R.string.email_verification_message), style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(Spacing.xl))

        ChannelTextField(
            value = uiState.code,
            onValueChange = onCodeChanged,
            label = stringResource(R.string.auth_code_label),
            isError = uiState.codeError != null,
            supportingText = uiState.codeError?.message(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
        )

        uiState.verifyError?.let { error ->
            Spacer(Modifier.height(Spacing.m))
            Text(error.message(), color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(Spacing.l))
        PrimaryButton(
            text = stringResource(R.string.email_verification_verify_button),
            onClick = onVerify,
            enabled = uiState.canVerify,
            isLoading = uiState.isVerifying,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.m))
        TextButton(onClick = onResendCode, enabled = !uiState.isResending) {
            Text(stringResource(R.string.email_verification_resend_button))
        }

        uiState.resendError?.let { error ->
            Text(error.message(), color = MaterialTheme.colorScheme.error)
        }
        if (uiState.resendConfirmationVisible) {
            Text(stringResource(R.string.email_verification_code_sent), style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(Spacing.l))
        TextButton(onClick = onLogout) {
            Text(stringResource(R.string.email_verification_logout_button))
        }
    }
}
