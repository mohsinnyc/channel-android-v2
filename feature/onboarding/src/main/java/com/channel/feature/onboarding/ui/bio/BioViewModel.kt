package com.channel.feature.onboarding.ui.bio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.toNetworkError
import com.channel.feature.onboarding.data.OnboardingRepository
import com.channel.feature.onboarding.ui.OnboardingStepEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BioViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BioUiState())
    val uiState: StateFlow<BioUiState> = _uiState

    private val _events = Channel<OnboardingStepEvent>(Channel.BUFFERED)
    val events: Flow<OnboardingStepEvent> = _events.receiveAsFlow()

    fun onBioChanged(value: String) {
        if (value.length <= BioUiState.MAX_LENGTH) {
            _uiState.update { it.copy(bio = value, submitError = null) }
        }
    }

    fun submit() {
        val bio = _uiState.value.bio.trim()
        if (bio.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null) }
            when (val result = onboardingRepository.submitBio(bio)) {
                is ApiResult.Success -> _events.send(OnboardingStepEvent.Continue)
                is ApiResult.Error -> _uiState.update { it.copy(isSubmitting = false, submitError = result.toNetworkError()) }
                is ApiResult.Exception -> _uiState.update { it.copy(isSubmitting = false, submitError = result.toNetworkError()) }
            }
        }
    }

    fun skip() {
        viewModelScope.launch { _events.send(OnboardingStepEvent.Continue) }
    }
}
