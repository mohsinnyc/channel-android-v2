package com.channel.feature.onboarding.ui.audio

import com.channel.core.audio.playback.PlaybackState
import com.channel.core.audio.recording.RecorderState
import com.channel.core.network.api.NetworkError
import java.io.File

data class AudioUiState(
    val recorderState: RecorderState = RecorderState.Idle,
    val playbackState: PlaybackState = PlaybackState(),
    val isSubmitting: Boolean = false,
    val submitError: NetworkError? = null,
) {
    val recordedFile: File? get() = (recorderState as? RecorderState.Stopped)?.file
    val canContinue: Boolean get() = recordedFile != null && !isSubmitting
}
