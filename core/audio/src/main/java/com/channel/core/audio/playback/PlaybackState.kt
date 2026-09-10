package com.channel.core.audio.playback

data class PlaybackState(
    val mediaId: String? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
)
