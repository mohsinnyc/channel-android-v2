package com.channel.feature.profile.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.channel.core.audio.playback.AudioPlayer
import com.channel.core.audio.playback.MediaIds
import com.channel.core.audio.playback.togglePauseResume
import com.channel.core.audio.playback.toggleStopReset
import com.channel.core.network.api.ApiResult
import com.channel.core.network.api.NetworkError
import com.channel.core.network.api.toNetworkError
import com.channel.core.network.session.AuthStateManager
import com.channel.core.posts.model.PlayableAudio
import com.channel.core.posts.model.toUiModel
import com.channel.feature.profile.data.ProfileRepository
import com.channel.feature.profile.data.ProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
            posts = load.profile?.posts.orEmpty().map { it.toUiModel() },
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
        val profile = uiState.value.profile ?: return
        val audioUrl = profile.audioBioUrl
        if (audioUrl.isBlank()) return
        audioPlayer.toggleStopReset(MediaIds.profileAudio(profile.userId), Uri.parse(audioUrl))
    }

    /** Ordinary post/embedded-quote audio: tapping again resumes rather than restarting. */
    fun toggleAudio(audio: PlayableAudio) {
        audioPlayer.togglePauseResume(audio.mediaId, Uri.parse(audio.url))
    }

    fun logout() {
        viewModelScope.launch {
            authStateManager.logout()
        }
    }
}
