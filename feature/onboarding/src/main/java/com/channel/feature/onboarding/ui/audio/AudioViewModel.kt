package com.channel.feature.onboarding.ui.audio

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.channel.core.audio.playback.AudioPlayer
import com.channel.core.audio.recording.AudioRecorder
import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.NetworkError
import com.channel.core.network.api.toNetworkError
import com.channel.feature.onboarding.data.OnboardingRepository
import com.channel.feature.onboarding.ui.OnboardingStepEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer,
    private val onboardingRepository: OnboardingRepository,
) : ViewModel() {

    private data class SubmitState(val isSubmitting: Boolean = false, val submitError: NetworkError? = null)

    private val _submitState = MutableStateFlow(SubmitState())

    val uiState: StateFlow<AudioUiState> = combine(
        audioRecorder.state,
        audioPlayer.state,
        _submitState,
    ) { recorderState, playbackState, submitState ->
        AudioUiState(recorderState, playbackState, submitState.isSubmitting, submitState.submitError)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AudioUiState())

    private val _events = Channel<OnboardingStepEvent>(Channel.BUFFERED)
    val events: Flow<OnboardingStepEvent> = _events.receiveAsFlow()

    fun startRecording() {
        audioPlayer.stop()
        audioRecorder.start()
    }

    fun stopRecording() {
        audioRecorder.stop()
    }

    fun reRecord() {
        audioPlayer.stop()
        audioRecorder.reset()
    }

    fun togglePlayback() {
        val state = uiState.value
        val file = state.recordedFile ?: return
        if (state.playbackState.isPlaying) {
            audioPlayer.pause()
        } else {
            audioPlayer.play(Uri.fromFile(file), mediaId = file.absolutePath)
        }
    }

    fun submit() {
        val file = uiState.value.recordedFile ?: return
        viewModelScope.launch {
            _submitState.value = SubmitState(isSubmitting = true)
            when (val result = onboardingRepository.submitAudio(file)) {
                is ApiResult.Success -> _events.send(OnboardingStepEvent.Continue)
                is ApiResult.Error -> _submitState.value = SubmitState(submitError = result.toNetworkError())
                is ApiResult.Exception -> _submitState.value = SubmitState(submitError = result.toNetworkError())
            }
        }
    }

    override fun onCleared() {
        audioPlayer.stop()
    }
}
