package com.channel.core.audio.recording

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.SystemClock
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * One recording session at a time — starting a new one implicitly discards
 * whatever the previous session produced (matches "press record to start
 * over" UX; the caller decides whether that's reachable).
 */
@Singleton
class AudioRecorder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var tickerJob: Job? = null
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var startedAtElapsedRealtime: Long = 0L

    private val _state = MutableStateFlow<RecorderState>(RecorderState.Idle)
    val state: StateFlow<RecorderState> = _state

    fun start() {
        releaseRecorder()
        val file = File(context.cacheDir, "recordings").apply { mkdirs() }.resolve("${UUID.randomUUID()}.m4a")

        val mediaRecorder = newMediaRecorder()
        try {
            mediaRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128_000)
                setAudioSamplingRate(44_100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            _state.value = RecorderState.Error(e)
            return
        }

        recorder = mediaRecorder
        outputFile = file
        startedAtElapsedRealtime = SystemClock.elapsedRealtime()
        _state.value = RecorderState.Recording(elapsedMs = 0L, amplitude = 0f)
        startTicker()
    }

    fun stop() {
        tickerJob?.cancel()
        val activeRecorder = recorder
        val file = outputFile
        if (activeRecorder == null || file == null) return

        val durationMs = SystemClock.elapsedRealtime() - startedAtElapsedRealtime
        try {
            activeRecorder.stop()
            activeRecorder.release()
        } catch (e: Exception) {
            // stop() throws if called too soon after start() with no data captured.
            releaseRecorder()
            _state.value = RecorderState.Error(e)
            return
        }
        recorder = null
        _state.value = RecorderState.Stopped(file, durationMs)
    }

    /** Discards the current or just-finished recording and returns to Idle. */
    fun reset() {
        tickerJob?.cancel()
        releaseRecorder()
        outputFile?.delete()
        outputFile = null
        _state.value = RecorderState.Idle
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                delay(100)
                val elapsed = SystemClock.elapsedRealtime() - startedAtElapsedRealtime
                _state.value = RecorderState.Recording(elapsed, normalizedAmplitude())
            }
        }
    }

    private fun normalizedAmplitude(): Float {
        val max = try {
            recorder?.maxAmplitude ?: 0
        } catch (_: Exception) {
            0
        }
        return (max / 32767f).coerceIn(0f, 1f)
    }

    private fun newMediaRecorder(): MediaRecorder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

    private fun releaseRecorder() {
        recorder?.let {
            try { it.stop() } catch (_: Exception) { /* may not have been started */ }
            try { it.release() } catch (_: Exception) { /* already released */ }
        }
        recorder = null
    }
}
