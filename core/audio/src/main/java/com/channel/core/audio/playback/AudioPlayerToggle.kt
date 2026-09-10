package com.channel.core.audio.playback

import android.net.Uri

/**
 * Tap-to-toggle for ordinary media consumption (posts, comments, replies):
 * tapping again resumes from where playback left off.
 */
fun AudioPlayer.togglePauseResume(mediaId: String, uri: Uri) {
    val current = state.value
    if (current.mediaId == mediaId && current.isPlaying) pause() else play(uri, mediaId)
}

/**
 * Tap-to-toggle for a short preview control (e.g. a profile's own voice-bio
 * badge): tapping again while playing stops and resets to the start, rather
 * than pausing/resuming — there is deliberately no "remember my position".
 */
fun AudioPlayer.toggleStopReset(mediaId: String, uri: Uri) {
    val current = state.value
    if (current.mediaId == mediaId && current.isPlaying) stop() else play(uri, mediaId)
}
