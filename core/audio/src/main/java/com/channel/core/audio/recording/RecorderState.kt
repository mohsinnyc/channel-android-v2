package com.channel.core.audio.recording

import java.io.File

sealed interface RecorderState {
    data object Idle : RecorderState
    data class Recording(val elapsedMs: Long, val amplitude: Float) : RecorderState
    data class Stopped(val file: File, val durationMs: Long) : RecorderState
    data class Error(val throwable: Throwable) : RecorderState
}
