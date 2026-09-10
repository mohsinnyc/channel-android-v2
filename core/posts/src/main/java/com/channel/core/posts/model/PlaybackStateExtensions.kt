package com.channel.core.posts.model

import com.channel.core.audio.playback.PlaybackState

/** Whether [audio] is the thing this [PlaybackState] is currently playing. */
fun PlaybackState.isPlayingAudio(audio: PlayableAudio?): Boolean =
    audio != null && isPlaying && mediaId == audio.mediaId
