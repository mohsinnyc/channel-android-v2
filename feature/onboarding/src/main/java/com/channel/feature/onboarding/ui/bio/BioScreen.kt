package com.channel.feature.onboarding.ui.bio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.channel.core.designsystem.components.PrimaryButton
import com.channel.core.designsystem.theme.Spacing
import com.channel.feature.onboarding.R
import com.channel.feature.onboarding.ui.components.message

@Composable
fun BioScreen(
    uiState: BioUiState,
    onBioChanged: (String) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.onboarding_bio_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(Spacing.s))
        Text(stringResource(R.string.onboarding_bio_subtitle), style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(Spacing.xl))

        OutlinedTextField(
            value = uiState.bio,
            onValueChange = onBioChanged,
            label = { Text(stringResource(R.string.onboarding_bio_label)) },
            supportingText = {
                Text(
                    stringResource(R.string.onboarding_bio_remaining_characters, uiState.remainingCharacters),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            minLines = 3,
            maxLines = 6,
            modifier = Modifier.fillMaxWidth(),
        )

        uiState.submitError?.let { error ->
            Spacer(Modifier.height(Spacing.m))
            Text(error.message(), color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(Spacing.xl))
        PrimaryButton(
            text = stringResource(R.string.onboarding_continue),
            onClick = onContinue,
            enabled = uiState.bio.isNotBlank() && !uiState.isSubmitting,
            isLoading = uiState.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        )

        TextButton(onClick = onSkip, enabled = !uiState.isSubmitting) {
            Text(stringResource(R.string.onboarding_skip))
        }
    }
}
