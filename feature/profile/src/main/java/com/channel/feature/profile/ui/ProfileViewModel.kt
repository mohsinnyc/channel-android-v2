package com.channel.feature.profile.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.channel.core.audio.playback.AudioPlayer
import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.NetworkError
import com.channel.core.network.api.toNetworkError
import com.channel.core.network.session.AuthStateManager
import com.channel.feature.profile.data.PostSummary
import com.channel.feature.profile.data.ProfileRepository
import com.channel.feature.profile.data.ProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val audioPlayer: AudioPlayer,
    private val authStateManager: AuthStateManager,
) : ViewModel() {

    private data class LoadState(
        val isLoading: Boolean = true,
        val profile: ProfileResponse? = null,
        val loadError: NetworkError? = null,
    )

    private val _loadState = MutableStateFlow(LoadState())

    val uiState: StateFlow<ProfileUiState> = combine(_loadState, audioPlayer.state) { load, playback ->
        ProfileUiState(
            isLoading = load.isLoading,
            profile = load.profile,
            loadError = load.loadError,
            playback = playback,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _loadState.value = LoadState(isLoading = true)
            when (val result = profileRepository.getMine()) {
                is ApiResult.Success -> _loadState.value = LoadState(isLoading = false, profile = result.data)
                is ApiResult.Error -> _loadState.value = LoadState(isLoading = false, loadError = result.toNetworkError())
                is ApiResult.Exception -> _loadState.value = LoadState(isLoading = false, loadError = result.toNetworkError())
            }
        }
    }

    /** Play toggles to stop (not pause): tapping again resets to the start rather than resuming. */
    fun toggleVoiceBioPlayback() {
        val audioUrl = uiState.value.profile?.audioBioUrl
        if (audioUrl.isNullOrBlank()) return
        togglePlayback(audioUrl)
    }

    fun togglePostPlayback(post: PostSummary) {
        val audioUrl = post.audioUrl ?: return
        togglePlayback(audioUrl)
    }

    private fun togglePlayback(mediaId: String) {
        val playback = audioPlayer.state.value
        if (playback.mediaId == mediaId && playback.isPlaying) {
            audioPlayer.stop()
        } else {
            audioPlayer.play(Uri.parse(mediaId), mediaId = mediaId)
        }
    }

    fun onLeaveScreen() {
        audioPlayer.stop()
    }

    fun logout() {
        viewModelScope.launch {
            authStateManager.logout()
        }
    }
}
