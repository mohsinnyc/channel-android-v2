package com.channel.feature.auth.ui.resetpassword

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
import com.channel.core.designsystem.components.PasswordField
import com.channel.core.designsystem.components.PrimaryButton
import com.channel.core.designsystem.theme.Spacing
import com.channel.feature.auth.R
import com.channel.feature.auth.ui.components.message

@Composable
fun ResetPasswordScreen(
    uiState: ResetPasswordUiState,
    onCodeChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onUseDifferentEmail: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.auth_reset_password_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(Spacing.s))
        Text(
            stringResource(R.string.auth_reset_password_subtitle, uiState.email),
            style = MaterialTheme.typography.bodyMedium,
        )
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
        Spacer(Modifier.height(Spacing.m))

        PasswordField(
            value = uiState.newPassword,
            onValueChange = onNewPasswordChanged,
            label = stringResource(R.string.auth_new_password_label),
            isError = uiState.passwordError != null,
            supportingText = uiState.passwordError?.message(),
            modifier = Modifier.fillMaxWidth(),
        )

        uiState.submitError?.let { error ->
            Spacer(Modifier.height(Spacing.m))
            Text(error.message(), color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(Spacing.l))
        PrimaryButton(
            text = stringResource(R.string.auth_reset_password_button),
            onClick = onSubmit,
            enabled = uiState.canSubmit,
            isLoading = uiState.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        )

        TextButton(onClick = onUseDifferentEmail) {
            Text(stringResource(R.string.auth_use_different_email))
        }
    }
}
