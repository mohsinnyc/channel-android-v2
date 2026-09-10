package com.channel.feature.onboarding.ui.audio

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.channel.core.audio.playback.PlaybackState
import com.channel.core.audio.recording.RecorderState
import com.channel.core.audio.recording.rememberRequestRecordAudioPermission
import com.channel.core.designsystem.components.PrimaryButton
import com.channel.core.designsystem.theme.Spacing
import com.channel.feature.onboarding.R
import com.channel.feature.onboarding.ui.components.message

@Composable
fun AudioScreen(
    uiState: AudioUiState,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onReRecord: () -> Unit,
    onTogglePlayback: () -> Unit,
    onContinue: () -> Unit,
) {
    val requestPermissionAndRecord = rememberRequestRecordAudioPermission(onGranted = onStartRecording)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.onboarding_audio_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(Spacing.s))
        Text(stringResource(R.string.onboarding_audio_subtitle), style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(Spacing.xxl))

        when (val recorderState = uiState.recorderState) {
            RecorderState.Idle ->
                RecordButton(onClick = requestPermissionAndRecord)

            is RecorderState.Recording ->
                RecordingInProgress(recorderState, onStop = onStopRecording)

            is RecorderState.Stopped ->
                PlaybackReview(
                    durationMs = recorderState.durationMs,
                    playbackState = uiState.playbackState,
                    onTogglePlayback = onTogglePlayback,
                    onReRecord = onReRecord,
                )

            is RecorderState.Error ->
                RecordButton(onClick = requestPermissionAndRecord)
        }

        uiState.submitError?.let { error ->
            Spacer(Modifier.height(Spacing.m))
            Text(error.message(), color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(Spacing.xxl))
        PrimaryButton(
            text = stringResource(R.string.onboarding_continue),
            onClick = onContinue,
            enabled = uiState.canContinue,
            isLoading = uiState.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        )
        // Deliberately no skip action here — audio is the one required onboarding step.
    }
}

@Composable
private fun RecordButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Mic,
            contentDescription = stringResource(R.string.onboarding_audio_record_action),
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(40.dp),
        )
    }
    Spacer(Modifier.height(Spacing.m))
    Text(stringResource(R.string.onboarding_audio_tap_to_record), style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun RecordingInProgress(state: RecorderState.Recording, onStop: () -> Unit) {
    val scale by animateFloatAsState(targetValue = 1f + state.amplitude * 0.35f, label = "amplitude")

    Box(
        modifier = Modifier
            .size(96.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.error)
            .clickable(onClick = onStop),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Stop,
            contentDescription = stringResource(R.string.onboarding_audio_stop_action),
            tint = MaterialTheme.colorScheme.onError,
            modifier = Modifier.size(32.dp),
        )
    }
    Spacer(Modifier.height(Spacing.m))
    Text(formatMillis(state.elapsedMs), style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun PlaybackReview(
    durationMs: Long,
    playbackState: PlaybackState,
    onTogglePlayback: () -> Unit,
    onReRecord: () -> Unit,
) {
    val effectiveDuration = if (playbackState.durationMs > 0) playbackState.durationMs else durationMs
    val progress = if (effectiveDuration > 0) {
        (playbackState.positionMs.toFloat() / effectiveDuration).coerceIn(0f, 1f)
    } else {
        0f
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onTogglePlayback) {
            if (playbackState.isBuffering) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Icon(
                    imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = stringResource(
                        if (playbackState.isPlaying) R.string.onboarding_audio_pause_action
                        else R.string.onboarding_audio_play_action
                    ),
                )
            }
        }
        Spacer(Modifier.width(Spacing.s))
        Column(modifier = Modifier.width(200.dp)) {
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            Text(formatMillis(effectiveDuration), style = MaterialTheme.typography.bodySmall)
        }
    }

    Spacer(Modifier.height(Spacing.m))
    TextButton(onClick = onReRecord) {
        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(Spacing.xs))
        Text(stringResource(R.string.onboarding_audio_rerecord_action))
    }
}

private fun formatMillis(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
