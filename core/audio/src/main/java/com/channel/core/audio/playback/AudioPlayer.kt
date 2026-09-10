package com.channel.core.audio.playback

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * One shared player for the whole app — every screen that plays a voice clip
 * (feed, profile bio, onboarding review, comments...) reads from the same
 * [state] instead of each maintaining its own, so "what's currently playing"
 * has exactly one owner instead of being re-derived per screen. [mediaId] is
 * real content identity (e.g. "post:<postId>"), never screen-scoped, so the
 * same content playing shows as playing everywhere it's rendered.
 *
 * Disk-caches played clips (100MB LRU) so replays don't re-download, and
 * hands audio focus to ExoPlayer so playback correctly pauses/ducks for
 * calls and other apps instead of fighting them.
 */
@Singleton
class AudioPlayer @Inject constructor(
    @ApplicationContext context: Context
) {
    private val cache = SimpleCache(
        File(context.cacheDir, "audio_cache"),
        LeastRecentlyUsedCacheEvictor(CACHE_SIZE_BYTES),
        StandaloneDatabaseProvider(context),
    )
    private val cacheDataSourceFactory = CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())

    private val exoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build(),
            /* handleAudioFocus = */ true,
        )
        .build()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var progressJob: Job? = null

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) startProgressTicker() else progressJob?.cancel()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _state.update {
                    it.copy(
                        isBuffering = playbackState == Player.STATE_BUFFERING,
                        durationMs = exoPlayer.duration.coerceAtLeast(0),
                    )
                }
                if (playbackState == Player.STATE_ENDED) {
                    exoPlayer.seekTo(0)
                    exoPlayer.pause()
                }
            }
        })
    }

    fun play(uri: Uri, mediaId: String) {
        if (_state.value.mediaId == mediaId) {
            exoPlayer.play()
            return
        }
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.play()
        _state.value = PlaybackState(mediaId = mediaId, isPlaying = true)
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }

    fun stop() {
        exoPlayer.stop()
        progressJob?.cancel()
        _state.value = PlaybackState()
    }

    private fun startProgressTicker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                _state.update { it.copy(positionMs = exoPlayer.currentPosition) }
                delay(200)
            }
        }
    }

    private companion object {
        const val CACHE_SIZE_BYTES = 100L * 1024 * 1024
    }
}
