package com.channel.feature.onboarding.ui.profileimage

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.channel.core.media.ImageCompressor
import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.toNetworkError
import com.channel.feature.onboarding.data.OnboardingRepository
import com.channel.feature.onboarding.ui.OnboardingStepEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileImageViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileImageUiState())
    val uiState: StateFlow<ProfileImageUiState> = _uiState

    private val _events = Channel<OnboardingStepEvent>(Channel.BUFFERED)
    val events: Flow<OnboardingStepEvent> = _events.receiveAsFlow()

    fun onImagePicked(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPick = true, submitError = null) }
            val file = runCatching { ImageCompressor.compress(context, uri) }.getOrNull()
            _uiState.update { it.copy(isProcessingPick = false, imageFile = file ?: it.imageFile) }
        }
    }

    fun submit() {
        val file = _uiState.value.imageFile ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitError = null) }
            when (val result = onboardingRepository.submitImage(file)) {
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
