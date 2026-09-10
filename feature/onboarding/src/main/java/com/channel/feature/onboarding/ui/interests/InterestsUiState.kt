package com.channel.feature.onboarding.ui.interests

import com.channel.core.network.api.NetworkError
import com.channel.feature.onboarding.data.Category

data class InterestsUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategoryIds: Set<String> = emptySet(),
    val isLoadingCategories: Boolean = false,
    val loadError: NetworkError? = null,
    val isSubmitting: Boolean = false,
    val submitError: NetworkError? = null,
)
