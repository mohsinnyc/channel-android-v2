package com.channel.feature.onboarding.ui.profileimage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.channel.core.designsystem.components.PrimaryButton
import com.channel.core.designsystem.theme.Spacing
import com.channel.core.media.rememberImagePicker
import com.channel.feature.onboarding.R
import com.channel.feature.onboarding.ui.components.message

@Composable
fun ProfileImageScreen(
    uiState: ProfileImageUiState,
    onImagePicked: (android.net.Uri) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
) {
    val imagePicker = rememberImagePicker(onImagePicked = onImagePicked)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.onboarding_image_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(Spacing.s))
        Text(stringResource(R.string.onboarding_image_subtitle), style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(Spacing.xl))

        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.imageFile != null) {
                AsyncImage(
                    model = uiState.imageFile,
                    contentDescription = stringResource(R.string.onboarding_image_preview_description),
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (uiState.isProcessingPick) {
                CircularProgressIndicator()
            }
        }

        Spacer(Modifier.height(Spacing.xl))

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.m)) {
            OutlinedButton(onClick = imagePicker.launchCamera) {
                Text(stringResource(R.string.onboarding_take_photo))
            }
            OutlinedButton(onClick = imagePicker.launchGallery) {
                Text(stringResource(R.string.onboarding_choose_from_gallery))
            }
        }

        uiState.submitError?.let { error ->
            Spacer(Modifier.height(Spacing.m))
            Text(error.message(), color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(Spacing.xl))
        PrimaryButton(
            text = stringResource(R.string.onboarding_continue),
            onClick = onContinue,
            enabled = uiState.canContinue,
            isLoading = uiState.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        )

        TextButton(onClick = onSkip, enabled = !uiState.isSubmitting) {
            Text(stringResource(R.string.onboarding_skip))
        }
    }
}
