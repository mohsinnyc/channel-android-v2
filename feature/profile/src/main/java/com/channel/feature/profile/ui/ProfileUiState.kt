package com.channel.feature.profile.ui

import com.channel.core.audio.playback.PlaybackState
import com.channel.core.network.api.NetworkError
import com.channel.core.posts.model.PostUiModel
import com.channel.feature.profile.data.ProfileResponse

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: ProfileResponse? = null,
    val posts: List<PostUiModel> = emptyList(),
    val loadError: NetworkError? = null,
    val playback: PlaybackState = PlaybackState(),
)
