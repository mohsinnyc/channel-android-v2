package com.channel.feature.onboarding.ui.interests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.channel.core.designsystem.components.PrimaryButton
import com.channel.core.designsystem.theme.Spacing
import com.channel.feature.onboarding.R
import com.channel.feature.onboarding.data.Category
import com.channel.feature.onboarding.ui.components.message

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestsScreen(
    uiState: InterestsUiState,
    onToggleCategory: (String) -> Unit,
    onFinish: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.onboarding_interests_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(Spacing.s))
        Text(stringResource(R.string.onboarding_interests_subtitle), style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(Spacing.xl))

        when {
            uiState.isLoadingCategories -> CircularProgressIndicator()
            uiState.loadError != null -> Text(uiState.loadError.message(), color = MaterialTheme.colorScheme.error)
            else -> FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                verticalArrangement = Arrangement.spacedBy(Spacing.s),
            ) {
                uiState.categories.forEach { category ->
                    CategoryChip(
                        category = category,
                        selected = category.id in uiState.selectedCategoryIds,
                        onClick = { onToggleCategory(category.id) },
                    )
                }
            }
        }

        uiState.submitError?.let { error ->
            Spacer(Modifier.height(Spacing.m))
            Text(error.message(), color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(Spacing.xl))
        val label = if (uiState.selectedCategoryIds.isEmpty()) {
            stringResource(R.string.onboarding_skip)
        } else {
            stringResource(R.string.onboarding_interests_finish)
        }
        PrimaryButton(
            text = label,
            onClick = onFinish,
            enabled = !uiState.isSubmitting,
            isLoading = uiState.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CategoryChip(category: Category, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(category.label) },
    )
}
