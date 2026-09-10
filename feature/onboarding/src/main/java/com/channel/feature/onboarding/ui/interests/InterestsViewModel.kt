package com.channel.feature.onboarding.ui.interests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.toNetworkError
import com.channel.feature.onboarding.data.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterestsViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InterestsUiState())
    val uiState: StateFlow<InterestsUiState> = _uiState

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCategories = true, loadError = null) }
            when (val result = onboardingRepository.listInterests()) {
                is ApiResult.Success -> _uiState.update { it.copy(isLoadingCategories = false, categories = result.data) }
                is ApiResult.Error -> _uiState.update { it.copy(isLoadingCategories = false, loadError = result.toNetworkError()) }
                is ApiResult.Exception -> _uiState.update { it.copy(isLoadingCategories = false, loadError = result.toNetworkError()) }
            }
        }
    }

    fun toggleCategory(id: String) {
        _uiState.update {
            val selection = if (id in it.selectedCategoryIds) it.selectedCategoryIds - id else it.selectedCategoryIds + id
            it.copy(selectedCategoryIds = selection)
        }
    }

    /**
     * Also how "skip" works — an empty selection is valid and still completes
     * onboarding, since this is the one call that flips onboardingState server-side.
     * On success there's nothing further to do here: OnboardingRepository already
     * triggers the app root's status refresh, which unmounts this whole flow.
     */
    fun finish() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null) }
            val categoryIds = _uiState.value.selectedCategoryIds.toList()
            when (val result = onboardingRepository.submitInterests(categoryIds)) {
                is ApiResult.Success -> Unit
                is ApiResult.Error -> _uiState.update { it.copy(isSubmitting = false, submitError = result.toNetworkError()) }
                is ApiResult.Exception -> _uiState.update { it.copy(isSubmitting = false, submitError = result.toNetworkError()) }
            }
        }
    }
}
