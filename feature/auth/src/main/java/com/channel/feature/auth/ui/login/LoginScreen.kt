package com.channel.feature.auth.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.channel.core.designsystem.components.ChannelTextField
import com.channel.core.designsystem.components.PasswordField
import com.channel.core.designsystem.components.PrimaryButton
import com.channel.core.designsystem.theme.Spacing
import com.channel.core.network.api.NetworkError
import com.channel.feature.auth.R
import com.channel.feature.auth.ui.components.message

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.auth_login_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(Spacing.xl))

        ChannelTextField(
            value = uiState.username,
            onValueChange = onUsernameChanged,
            label = stringResource(R.string.auth_username_label),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Spacing.m))

        PasswordField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            label = stringResource(R.string.auth_password_label),
            modifier = Modifier.fillMaxWidth(),
        )

        uiState.submitError?.let { error ->
            Spacer(Modifier.height(Spacing.m))
            // A 401 here means wrong credentials, not the "session expired" reading
            // the shared NetworkError.message() uses everywhere else.
            val message = if (error == NetworkError.Unauthorized) {
                stringResource(R.string.auth_invalid_credentials)
            } else {
                error.message()
            }
            Text(message, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(Spacing.xs))
        TextButton(onClick = onNavigateToForgotPassword) {
            Text(stringResource(R.string.auth_forgot_password))
        }

        Spacer(Modifier.height(Spacing.l))
        PrimaryButton(
            text = stringResource(R.string.auth_login_button),
            onClick = onSubmit,
            enabled = uiState.canSubmit,
            isLoading = uiState.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        )

        TextButton(onClick = onNavigateToSignup) {
            Text(stringResource(R.string.auth_no_account))
        }
    }
}
